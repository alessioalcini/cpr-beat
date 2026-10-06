package dev.alcini.cprbeat.engine

/** Beat timing math. Pure Kotlin, no Android dependencies, fully unit-tested. */
object Tempo {
    const val MIN_BPM = 100
    const val MAX_BPM = 120
    const val DEFAULT_BPM = 110

    /** Length of one beat in milliseconds at the given tempo. */
    fun periodMillis(bpm: Int): Double {
        require(bpm > 0) { "bpm must be positive, was $bpm" }
        return 60_000.0 / bpm
    }

    /** PCM frames in one beat at the given sample rate, rounded to the nearest frame. */
    fun framesPerBeat(bpm: Int, sampleRateHz: Int): Int {
        require(bpm > 0) { "bpm must be positive, was $bpm" }
        require(sampleRateHz > 0) { "sampleRateHz must be positive, was $sampleRateHz" }
        return Math.round(sampleRateHz * 60.0 / bpm).toInt()
    }
}
