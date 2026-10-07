package dev.alcini.cprbeat.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import dev.alcini.cprbeat.engine.CycleLayout
import dev.alcini.cprbeat.engine.CycleSpec
import dev.alcini.cprbeat.engine.PcmRenderer
import dev.alcini.cprbeat.engine.ToneBank
import dev.alcini.cprbeat.engine.entryFrameForModeChange
import dev.alcini.cprbeat.engine.mapFrameBetween

/**
 * Plays one rendered metronome cycle in a loop. The whole cycle lives in a static AudioTrack
 * buffer with loop points, so timing is decided by the audio hardware clock, not by timers.
 *
 * Audio goes out as an alarm ([AudioAttributes.USAGE_ALARM]): it plays in silent mode and under
 * the default Do Not Disturb rules, like an alarm clock.
 *
 * All methods are blocking and must be called off the main thread. Not thread-safe: the owner
 * serializes calls (the session view model does, on a single-threaded dispatcher).
 */
class ClickPlayer {
    /** One playing cycle: the track, its layout and the buffer frame playback began at. */
    private class Session(val track: AudioTrack, val layout: CycleLayout, val entryFrame: Int)

    @Volatile private var session: Session? = null
    var bank: ToneBank = ToneBank.DEFAULT

    val current: CycleLayout? get() = session?.layout
    val isPlaying: Boolean get() = session?.track?.playState == AudioTrack.PLAYSTATE_PLAYING

    /** Native output rate of the alarm stream; rendering at it avoids resampling. */
    val nativeSampleRateHz: Int
        get() = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_ALARM).takeIf { it > 0 }
            ?: CycleSpec.DEFAULT_SAMPLE_RATE_HZ

    /** Starts looping the cycle described by [spec] from buffer frame [startFrameInCycle]. */
    fun start(spec: CycleSpec, startFrameInCycle: Int = 0) {
        val newLayout = CycleLayout.of(spec)
        val pcm = PcmRenderer.render(newLayout, bank)
        val newTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(spec.sampleRateHz)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            )
            .setTransferMode(AudioTrack.MODE_STATIC)
            .setBufferSizeInBytes(pcm.size * Short.SIZE_BYTES)
            .build()
        check(newTrack.state == AudioTrack.STATE_NO_STATIC_DATA || newTrack.state == AudioTrack.STATE_INITIALIZED) {
            "AudioTrack failed to initialize (state ${newTrack.state})"
        }
        val written = newTrack.write(pcm, 0, pcm.size)
        check(written == pcm.size) { "AudioTrack.write returned $written of ${pcm.size} frames" }
        check(newTrack.setLoopPoints(0, pcm.size, -1) == AudioTrack.SUCCESS) { "setLoopPoints failed" }
        val entry = startFrameInCycle.coerceIn(0, pcm.size - 1)
        if (entry > 0) check(newTrack.setPlaybackHeadPosition(entry) == AudioTrack.SUCCESS) { "setPlaybackHeadPosition failed" }
        Log.d(TAG, "start mode=${spec.mode} bpm=${spec.bpm} total=${pcm.size} entry=$entry")

        val old = session
        old?.track?.let { runCatching { it.pause() } }
        session = Session(newTrack, newLayout, entry)
        newTrack.play()
        old?.let { release(it.track) }
    }

    /**
     * Switches to a new cycle without breaking the rhythm. A rate change keeps the rescuer's
     * place (same compression number, same fraction of the beat). A mode change keeps the phase
     * of the beat so the next click stays on the grid, and restarts the count at 1.
     */
    fun switchTo(spec: CycleSpec) {
        val old = session
        val target = CycleLayout.of(spec)
        val startFrame = when {
            old == null -> 0
            old.layout.spec.mode == spec.mode -> mapFrameBetween(frameInCycle(), old.layout, target)
            else -> entryFrameForModeChange(frameInCycle(), old.layout, target)
        }
        start(spec, startFrame)
    }

    /**
     * Current buffer frame inside the cycle, 0 until totalFrames; 0 when not playing.
     * Measured on Android 16: the head position counts frames played since play(), advances
     * monotonically across loop restarts and does not include the entry offset set with
     * setPlaybackHeadPosition, so the offset is added back here.
     */
    fun frameInCycle(): Int {
        val s = session ?: return 0
        val head = s.track.playbackHeadPosition.toLong() and 0xFFFF_FFFFL
        return ((head + s.entryFrame) % s.layout.totalFrames).toInt()
    }

    fun stop() {
        val old = session ?: return
        session = null
        release(old.track)
    }

    private fun release(track: AudioTrack) {
        runCatching { track.pause() }
        runCatching { track.flush() }
        runCatching { track.stop() }
        track.release()
    }

    private companion object { const val TAG = "ClickPlayer" }
}
