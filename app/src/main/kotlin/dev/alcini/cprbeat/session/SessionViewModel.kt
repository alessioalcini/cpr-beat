package dev.alcini.cprbeat.session

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.alcini.cprbeat.audio.ClickPlayer
import dev.alcini.cprbeat.audio.VolumeController
import dev.alcini.cprbeat.engine.CycleSpec
import dev.alcini.cprbeat.engine.Mode
import dev.alcini.cprbeat.engine.Tempo
import dev.alcini.cprbeat.engine.ToneBank
import dev.alcini.cprbeat.settings.Settings
import dev.alcini.cprbeat.settings.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Owns one CPR session: the metronome player, the rescuer-switch countdown, the elapsed time
 * and the per-session overrides of rate, mode and countdown. All public functions are called on
 * the main thread; player calls are serialized on a single-threaded dispatcher.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModel(app: Application) : AndroidViewModel(app) {
    private val settingsRepo = SettingsRepository(app)
    private val player = ClickPlayer()
    private val volume = VolumeController(app)
    private val audio = Dispatchers.Default.limitedParallelism(1)
    private val beats = BeatDetector()

    private var settings = Settings()
    private var timer: RescuerSwitchTimer? = null
    private var startedElapsedMillis = 0L
    private var bannerUntilMillis = 0L
    private var ticker: Job? = null

    private val _state = MutableStateFlow(SessionState())
    val state: StateFlow<SessionState> = _state.asStateFlow()

    private val _settings = MutableStateFlow(Settings())
    /** Persistent settings as shown on the Settings screen. */
    val settingsState: StateFlow<Settings> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepo.settings.collect { s ->
                settings = s
                _settings.value = s
                launch(audio) { player.bank = s.toneBank }
                if (!_state.value.running) {
                    _state.update {
                        it.copy(
                            bpm = s.defaultBpm,
                            countdown = s.defaultCountdown,
                            countdownRemainingMillis = s.defaultCountdown.millis.takeIf { s.defaultCountdown != CountdownOption.OFF },
                            breathPauseMillis = s.breathPauseMillis,
                            showHints = !s.hintsDismissed,
                            volumeLow = !s.autoMaxVolume && volume.level() < VolumeController.THRESHOLD,
                        )
                    }
                }
            }
        }
    }

    private fun specOf(st: SessionState) = CycleSpec(st.mode, st.bpm, st.breathPauseMillis, player.nativeSampleRateHz)

    fun start() {
        val st = _state.value
        if (st.running) return
        val now = SystemClock.elapsedRealtime()
        startedElapsedMillis = now
        bannerUntilMillis = 0
        timer = RescuerSwitchTimer(st.countdown, now)
        beats.reset()
        if (settings.autoMaxVolume) volume.raiseIfQuiet()
        _state.update {
            it.copy(
                running = true,
                elapsedMillis = 0,
                startedAtEpochMillis = System.currentTimeMillis(),
                bannerRemainingMillis = 0,
                countdownRemainingMillis = timer?.remaining(now),
                showHints = false,
                volumeLow = !settings.autoMaxVolume && volume.level() < VolumeController.THRESHOLD,
            )
        }
        if (!settings.hintsDismissed) viewModelScope.launch { settingsRepo.setHintsDismissed() }
        viewModelScope.launch(audio) {
            player.start(specOf(st))
            prewarm(st)
        }
        ticker = viewModelScope.launch {
            while (isActive) {
                delay(TICK_MILLIS)
                tick()
            }
        }
    }

    /** Renders the cycles a switch is likely to need so that switching never waits. */
    private fun prewarm(st: SessionState) {
        val other = if (st.mode == Mode.COMPRESSIONS) Mode.THIRTY_TWO else Mode.COMPRESSIONS
        player.prepare(specOf(st.copy(mode = other)))
        for (bpm in RATES) if (bpm != st.bpm) player.prepare(specOf(st.copy(bpm = bpm)))
    }

    fun stop() {
        val st = _state.value
        if (!st.running) return
        ticker?.cancel()
        ticker = null
        val now = SystemClock.elapsedRealtime()
        _state.update {
            it.copy(
                running = false,
                elapsedMillis = now - startedElapsedMillis,
                bannerRemainingMillis = 0,
                countdownRemainingMillis = it.countdown.millis.takeIf { _ -> it.countdown != CountdownOption.OFF },
            )
        }
        viewModelScope.launch(audio) { player.stop() }
        volume.restore()
    }

    /** The app left the foreground (Home, lock, call): the metronome stops, per SPEC 5.1. */
    fun onLeftForeground() = stop()

    private fun tick() {
        val t = timer ?: return
        val now = SystemClock.elapsedRealtime()
        val expiries = t.tick(now)
        if (expiries > 0) bannerUntilMillis = now + BANNER_MILLIS
        _state.update {
            it.copy(
                elapsedMillis = now - startedElapsedMillis,
                countdownRemainingMillis = t.remaining(now),
                bannerRemainingMillis = (bannerUntilMillis - now).coerceAtLeast(0),
                flashCount = it.flashCount + expiries,
            )
        }
    }

    fun setBpm(bpm: Int) {
        require(bpm in Tempo.MIN_BPM..Tempo.MAX_BPM)
        if (_state.value.bpm == bpm) return
        val st = _state.updateAndGet { it.copy(bpm = bpm) }
        if (st.running) viewModelScope.launch(audio) { player.switchTo(specOf(st)) }
    }

    fun setMode(mode: Mode) {
        if (_state.value.mode == mode) return
        val st = _state.updateAndGet { it.copy(mode = mode) }
        if (st.running) viewModelScope.launch(audio) { player.switchTo(specOf(st)); prewarm(st) }
    }

    /** Tapping the countdown cycles Off → 1 → 2 → 3 → 5 → Off for this session (SPEC 5.4). */
    fun cycleCountdown() {
        val now = SystemClock.elapsedRealtime()
        val next = _state.value.countdown.next()
        timer?.restart(next, now)
        bannerUntilMillis = 0
        _state.update {
            it.copy(
                countdown = next,
                countdownRemainingMillis = if (it.running) timer?.remaining(now) else next.millis.takeIf { next != CountdownOption.OFF },
                bannerRemainingMillis = 0,
            )
        }
    }

    fun setDefaultBpm(bpm: Int) { viewModelScope.launch { settingsRepo.setDefaultBpm(bpm) } }
    fun setDefaultCountdown(option: CountdownOption) { viewModelScope.launch { settingsRepo.setDefaultCountdown(option) } }
    fun setBreathPauseMillis(millis: Int) { viewModelScope.launch { settingsRepo.setBreathPauseMillis(millis) } }
    fun setAutoMaxVolume(on: Boolean) { viewModelScope.launch { settingsRepo.setAutoMaxVolume(on) } }
    fun setTonePreset(name: String) {
        viewModelScope.launch { settingsRepo.setTonePreset(name) }
        viewModelScope.launch(audio) { player.preview(ToneBank.byName(name), Tempo.DEFAULT_BPM, beats = 4, sampleRateHz = player.nativeSampleRateHz) }
    }

    fun dismissHints() {
        _state.update { it.copy(showHints = false) }
        viewModelScope.launch { settingsRepo.setHintsDismissed() }
    }

    /** Read by the UI on every frame while running; drives the counter and the pulse. */
    fun snapshot(): BeatSnapshot {
        val layout = player.current ?: return BeatSnapshot(null, beats.beatsHeard)
        val frame = player.frameInCycle()
        if (frame !in 0 until layout.totalFrames) return BeatSnapshot(null, beats.beatsHeard)
        beats.feed(layout, frame)
        return BeatSnapshot(layout.positionAt(frame), beats.beatsHeard)
    }

    override fun onCleared() {
        ticker?.cancel()
        player.stop()
        volume.restore()
    }

    private fun <T> MutableStateFlow<T>.updateAndGet(f: (T) -> T): T { update(f); return value }

    private companion object {
        const val TICK_MILLIS = 100L
        const val BANNER_MILLIS = 10_000L
        val RATES = listOf(100, 110, 120)
    }
}
