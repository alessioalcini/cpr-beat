package dev.alcini.cprbeat.engine

/** A synthesized sound: sine at [frequencyHz], [durationMillis] long, peak [amplitude] in 0..1. */
data class Tone(
    val frequencyHz: Double,
    val durationMillis: Int,
    val amplitude: Double,
    /** Percussive tones decay exponentially; sustained tones hold with short fades. */
    val percussive: Boolean,
) {
    init {
        require(frequencyHz > 0) { "frequencyHz must be positive" }
        require(durationMillis > 0) { "durationMillis must be positive" }
        require(amplitude in 0.0..1.0) { "amplitude must be in 0..1, was $amplitude" }
    }
}

/** The four sounds of the metronome. Numbers are the starting point; tune by ear. */
object ToneBank {
    val click = Tone(frequencyHz = 1_400.0, durationMillis = 40, amplitude = 0.85, percussive = true)
    val warningClick = Tone(frequencyHz = 1_900.0, durationMillis = 40, amplitude = 0.85, percussive = true)
    val breath = Tone(frequencyHz = 660.0, durationMillis = 250, amplitude = 0.7, percussive = false)
    val resume = Tone(frequencyHz = 1_000.0, durationMillis = 120, amplitude = 0.8, percussive = true)

    fun forEvent(kind: EventKind): Tone = when (kind) {
        EventKind.CLICK -> click
        EventKind.WARNING_CLICK -> warningClick
        EventKind.BREATH_TONE -> breath
        EventKind.RESUME_TONE -> resume
    }
}
