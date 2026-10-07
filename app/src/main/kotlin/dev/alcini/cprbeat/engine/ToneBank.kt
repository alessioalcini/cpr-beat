package dev.alcini.cprbeat.engine

/** One overtone of a tone: [harmonic] times the fundamental at relative [level]. */
data class Partial(val harmonic: Double, val level: Double)

/**
 * A synthesized sound. [amplitude] is the true rendered peak in 0..1: the renderer normalizes
 * the summed partials and the envelope to it, so levels are comparable across tones.
 * [decayMillis] set makes the tone percussive (exponential decay with that time constant);
 * null makes it sustained with an [attackMillis] ramp. Every tone ends with a [releaseMillis]
 * linear release to zero so nothing is cut mid-waveform.
 */
data class Tone(
    val frequencyHz: Double,
    val durationMillis: Int,
    val amplitude: Double,
    val partials: List<Partial> = listOf(Partial(1.0, 1.0)),
    val attackMillis: Double = 0.0,
    val decayMillis: Double? = null,
    val releaseMillis: Double = 2.0,
) {
    init {
        require(frequencyHz > 0) { "frequencyHz must be positive" }
        require(durationMillis > 0) { "durationMillis must be positive" }
        require(amplitude in 0.0..1.0) { "amplitude must be in 0..1, was $amplitude" }
        require(partials.isNotEmpty()) { "at least one partial" }
        require(attackMillis >= 0 && releaseMillis >= 0) { "attack and release must be non-negative" }
        require(decayMillis == null || decayMillis > 0) { "decayMillis must be positive when set" }
    }

    val percussive: Boolean get() = decayMillis != null
}

/** The four sounds of the metronome, as one named preset. Tune by ear on the audition screen. */
data class ToneBank(
    val name: String,
    val click: Tone,
    val warningClick: Tone,
    val breath: Tone,
    val resume: Tone,
) {
    fun forEvent(kind: EventKind): Tone = when (kind) {
        EventKind.CLICK -> click
        EventKind.WARNING_CLICK -> warningClick
        EventKind.BREATH_TONE -> breath
        EventKind.RESUME_TONE -> resume
    }

    companion object {
        private val woodPartials = listOf(Partial(1.0, 1.0), Partial(2.0, 0.5), Partial(3.0, 0.25))
        private val breathPartials = listOf(Partial(1.0, 1.0), Partial(2.0, 0.35))

        /** Breath cue: 450 ms so two of them and the resume tone fit even in a 3 s pause. */
        private fun breath(frequencyHz: Double) = Tone(
            frequencyHz = frequencyHz, durationMillis = 450, amplitude = 0.55,
            partials = breathPartials, attackMillis = 5.0, releaseMillis = 20.0,
        )

        private fun resume(frequencyHz: Double) = Tone(
            frequencyHz = frequencyHz, durationMillis = 100, amplitude = 0.85,
            partials = listOf(Partial(1.0, 1.0), Partial(2.0, 0.5)), decayMillis = 25.0,
        )

        /** Wood-block-like click: fundamental plus two overtones, 50 ms, 12 ms decay. */
        val WOOD = ToneBank(
            name = "Wood 880",
            click = Tone(880.0, 50, 0.95, woodPartials, decayMillis = 12.0),
            warningClick = Tone(1175.0, 60, 0.95, woodPartials, decayMillis = 12.0),
            breath = breath(740.0),
            resume = resume(880.0),
        )

        /** Same timbre, a fourth lower: softer on the ear, needs a speaker that reaches 660 Hz. */
        val LOW = ToneBank(
            name = "Low 660",
            click = Tone(660.0, 50, 0.95, listOf(Partial(1.0, 1.0), Partial(2.0, 0.6), Partial(3.0, 0.3)), decayMillis = 12.0),
            warningClick = Tone(880.0, 60, 0.95, woodPartials, decayMillis = 12.0),
            breath = breath(660.0),
            resume = resume(784.0),
        )

        /** Clean sine click at 1 kHz: closest to the first version, just lower and louder. */
        val CLEAN = ToneBank(
            name = "Clean 1000",
            click = Tone(1000.0, 40, 0.95, decayMillis = 10.0),
            warningClick = Tone(1320.0, 40, 0.95, decayMillis = 10.0),
            breath = breath(740.0),
            resume = resume(1000.0),
        )

        val DEFAULT = CLEAN
        val presets = listOf(CLEAN, WOOD, LOW)

        fun byName(name: String?): ToneBank = presets.firstOrNull { it.name == name } ?: DEFAULT
    }
}
