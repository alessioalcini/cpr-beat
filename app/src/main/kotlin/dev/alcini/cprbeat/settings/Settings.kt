package dev.alcini.cprbeat.settings

import dev.alcini.cprbeat.engine.CycleSpec
import dev.alcini.cprbeat.engine.Tempo
import dev.alcini.cprbeat.engine.ToneBank
import dev.alcini.cprbeat.session.CountdownOption

/** Persistent user settings with the factory defaults from SPEC.md section 6. */
data class Settings(
    val defaultBpm: Int = Tempo.DEFAULT_BPM,
    val defaultCountdown: CountdownOption = CountdownOption.DEFAULT,
    val breathPauseMillis: Int = CycleSpec.DEFAULT_BREATH_PAUSE_MILLIS,
    val autoMaxVolume: Boolean = true,
    val tonePreset: String = ToneBank.DEFAULT.name,
    val hintsDismissed: Boolean = false,
) {
    val toneBank: ToneBank get() = ToneBank.byName(tonePreset)
}
