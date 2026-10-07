package dev.alcini.cprbeat.session

import dev.alcini.cprbeat.engine.CycleLayout

/**
 * Turns successive playback frames into beat events for the pulse animation. Pure logic: feed it
 * the frame inside the cycle on every UI frame; it returns how many clicks happened since the
 * previous call (0 or 1 at any sane polling rate, more if the UI stalled).
 */
class BeatDetector {
    private var lastLayout: CycleLayout? = null
    private var lastBeatOrdinal: Long = -1
    private var cycles: Long = 0
    private var lastFrame: Int = 0

    /** Total clicks heard since START; drives the pulse animation key. */
    var beatsHeard: Long = 0
        private set

    fun reset() {
        lastLayout = null; lastBeatOrdinal = -1; cycles = 0; lastFrame = 0; beatsHeard = 0
    }

    /** Returns the number of new clicks since the previous call. */
    fun feed(layout: CycleLayout, frameInCycle: Int): Int {
        if (layout !== lastLayout) {
            // a switch: the new cycle's beat grid continues the count, nothing is "new" yet
            lastLayout = layout
            cycles = 0
            lastFrame = frameInCycle
            lastBeatOrdinal = ordinal(layout, frameInCycle)
            return 0
        }
        if (frameInCycle < lastFrame) cycles++
        lastFrame = frameInCycle
        val ordinal = ordinal(layout, frameInCycle)
        val newBeats = (ordinal - lastBeatOrdinal).coerceAtLeast(0).toInt()
        lastBeatOrdinal = ordinal
        beatsHeard += newBeats
        return newBeats
    }

    private fun ordinal(layout: CycleLayout, frameInCycle: Int): Long {
        val clicksPerCycle = layout.pauseStartFrame / layout.spec.beatFrames
        val inCycle = if (frameInCycle >= layout.pauseStartFrame) clicksPerCycle - 1L
        else (frameInCycle / layout.spec.beatFrames).toLong()
        return cycles * clicksPerCycle + inCycle
    }
}
