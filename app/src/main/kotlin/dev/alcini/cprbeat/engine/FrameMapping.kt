package dev.alcini.cprbeat.engine

/**
 * Maps a frame of [from] to the equivalent frame of [to]: the same beat ordinal and the same
 * fraction of the beat, or the same fraction of the pause when inside the pause. Used when the
 * rate changes mid-cycle so the rescuer keeps their place in the count.
 */
fun mapFrameBetween(frame: Int, from: CycleLayout, to: CycleLayout): Int {
    require(frame in 0 until from.totalFrames) { "frame must be in 0 until ${from.totalFrames}, was $frame" }
    val mapped = if (frame >= from.pauseStartFrame) {
        val fraction = (frame - from.pauseStartFrame).toDouble() / from.pauseFrames.coerceAtLeast(1)
        to.pauseStartFrame + (fraction * to.pauseFrames).toInt()
    } else {
        val beat = frame / from.spec.beatFrames
        val fraction = (frame % from.spec.beatFrames).toDouble() / from.spec.beatFrames
        beat * to.spec.beatFrames + (fraction * to.spec.beatFrames).toInt()
    }
    return mapped.coerceIn(0, to.totalFrames - 1)
}
