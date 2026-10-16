package dev.alcini.cprbeat.engine

/** What sounds at a given frame of the cycle. */
enum class EventKind { CLICK, WARNING_CLICK, BREATH_TONE, RESUME_TONE }

/**
 * One scheduled sound. [compression] is 1..30 for clicks and 0 for the breath and resume tones.
 */
data class CycleEvent(val kind: EventKind, val frame: Int, val compression: Int = 0)

/** Where the rescuer is inside the cycle, derived from the playback frame. */
sealed interface Position {
    /** Compression [number] of [total]; [warning] is true for the last five before the pause. */
    data class Compression(val number: Int, val total: Int, val warning: Boolean) : Position

    /** Inside the breath pause; [breathsCued] is how many breath tones have sounded so far (0..2). */
    data class BreathPause(val elapsedFrames: Int, val totalFrames: Int, val breathsCued: Int) : Position
}

/**
 * The sample-accurate timeline of one cycle. The audio player loops a PCM rendering of it,
 * so every click, breath cue and the pause length are fixed in frames and never drift.
 *
 * COMPRESSIONS: one beat per cycle, a click at frame 0.
 * THIRTY_TWO: 30 beats, click n at (n - 1) * beatFrames; the pause starts one beat after the
 * 30th click (when the 31st would have come) and lasts [CycleSpec.breathPauseMillis]; the next
 * cycle's first click follows immediately. Breath tones sound at 30 % and 65 % of the pause, so a
 * lone rescuer has time to reach the head before the first one; a resume tone sounds 0.5 s
 * before the pause ends.
 */
class CycleLayout private constructor(
    val spec: CycleSpec,
    val events: List<CycleEvent>,
    val totalFrames: Int,
    /** First frame of the breath pause; equals [totalFrames] when there is no pause. */
    val pauseStartFrame: Int,
) {
    val pauseFrames: Int get() = totalFrames - pauseStartFrame

    /** Position for a frame inside the cycle (0 until [totalFrames]). */
    fun positionAt(frameInCycle: Int): Position {
        require(frameInCycle in 0 until totalFrames) {
            "frameInCycle must be in 0 until $totalFrames, was $frameInCycle"
        }
        if (spec.mode == Mode.COMPRESSIONS) return Position.Compression(number = 1, total = 1, warning = false)
        if (frameInCycle >= pauseStartFrame) {
            val elapsed = frameInCycle - pauseStartFrame
            val cued = when {
                elapsed >= breathFrame(2) - pauseStartFrame -> 2
                elapsed >= breathFrame(1) - pauseStartFrame -> 1
                else -> 0
            }
            return Position.BreathPause(elapsedFrames = elapsed, totalFrames = pauseFrames, breathsCued = cued)
        }
        val number = frameInCycle / spec.beatFrames + 1
        return Position.Compression(
            number = number,
            total = CycleSpec.COMPRESSIONS_PER_CYCLE,
            warning = number > CycleSpec.COMPRESSIONS_PER_CYCLE - CycleSpec.WARNING_ZONE_SIZE,
        )
    }

    /** Frame of the n-th breath tone (1 or 2). */
    fun breathFrame(n: Int): Int = breathFrame(n, pauseStartFrame, pauseFrames)

    companion object {
        /** Where each breath tone sounds, as a fraction of the pause. */
        val BREATH_TONE_AT = listOf(0.30, 0.65)
        const val RESUME_BEFORE_END_MILLIS = 500

        private fun breathFrame(n: Int, pauseStart: Int, pauseFrames: Int): Int =
            pauseStart + Math.round(pauseFrames * BREATH_TONE_AT[n - 1]).toInt()

        fun of(spec: CycleSpec): CycleLayout {
            val beat = spec.beatFrames
            return when (spec.mode) {
                Mode.COMPRESSIONS -> CycleLayout(
                    spec = spec,
                    events = listOf(CycleEvent(EventKind.CLICK, frame = 0, compression = 1)),
                    totalFrames = beat,
                    pauseStartFrame = beat,
                )
                Mode.THIRTY_TWO -> {
                    val n = CycleSpec.COMPRESSIONS_PER_CYCLE
                    val pauseStart = n * beat
                    val total = pauseStart + spec.pauseFrames
                    val clicks = (1..n).map { c ->
                        val kind = if (c > n - CycleSpec.WARNING_ZONE_SIZE) EventKind.WARNING_CLICK else EventKind.CLICK
                        CycleEvent(kind, frame = (c - 1) * beat, compression = c)
                    }
                    val cues = listOf(
                        CycleEvent(EventKind.BREATH_TONE, frame = breathFrame(1, pauseStart, spec.pauseFrames)),
                        CycleEvent(EventKind.BREATH_TONE, frame = breathFrame(2, pauseStart, spec.pauseFrames)),
                        CycleEvent(EventKind.RESUME_TONE, frame = total - spec.millisToFrames(RESUME_BEFORE_END_MILLIS)),
                    )
                    CycleLayout(spec = spec, events = clicks + cues, totalFrames = total, pauseStartFrame = pauseStart)
                }
            }
        }
    }
}
