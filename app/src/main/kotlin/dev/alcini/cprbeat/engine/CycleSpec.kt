package dev.alcini.cprbeat.engine

/** Metronome mode. [THIRTY_TWO] is the 30 compressions : 2 breaths cycle. */
enum class Mode { COMPRESSIONS, THIRTY_TWO }

/**
 * Everything needed to lay out one metronome cycle. Immutable and validated on construction,
 * so every layout built from a spec is well-formed by construction.
 */
data class CycleSpec(
    val mode: Mode,
    val bpm: Int,
    val breathPauseMillis: Int = DEFAULT_BREATH_PAUSE_MILLIS,
    val sampleRateHz: Int = DEFAULT_SAMPLE_RATE_HZ,
) {
    init {
        require(bpm in Tempo.MIN_BPM..Tempo.MAX_BPM) {
            "bpm must be in ${Tempo.MIN_BPM}..${Tempo.MAX_BPM}, was $bpm"
        }
        require(breathPauseMillis in MIN_BREATH_PAUSE_MILLIS..MAX_BREATH_PAUSE_MILLIS) {
            "breathPauseMillis must be in $MIN_BREATH_PAUSE_MILLIS..$MAX_BREATH_PAUSE_MILLIS, was $breathPauseMillis"
        }
        require(sampleRateHz > 0) { "sampleRateHz must be positive, was $sampleRateHz" }
    }

    /** PCM frames per beat at this tempo and sample rate. */
    val beatFrames: Int get() = Tempo.framesPerBeat(bpm, sampleRateHz)

    /** PCM frames in the breath pause. Zero in [Mode.COMPRESSIONS]. */
    val pauseFrames: Int
        get() = if (mode == Mode.THIRTY_TWO) millisToFrames(breathPauseMillis) else 0

    fun millisToFrames(millis: Int): Int = Math.round(sampleRateHz * millis / 1000.0).toInt()

    companion object {
        const val DEFAULT_SAMPLE_RATE_HZ = 48_000
        const val DEFAULT_BREATH_PAUSE_MILLIS = 5_000
        const val MIN_BREATH_PAUSE_MILLIS = 3_000
        const val MAX_BREATH_PAUSE_MILLIS = 8_000
        const val COMPRESSIONS_PER_CYCLE = 30
        /** Compressions 26..30 are the warning zone before the breath pause. */
        const val WARNING_ZONE_SIZE = 5
    }
}
