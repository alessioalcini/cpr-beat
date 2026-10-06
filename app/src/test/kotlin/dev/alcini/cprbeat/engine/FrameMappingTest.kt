package dev.alcini.cprbeat.engine

import org.junit.Assert.assertEquals
import org.junit.Test

class FrameMappingTest {
    private val from = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 100, breathPauseMillis = 5_000))
    private val to = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 120, breathPauseMillis = 5_000))

    @Test
    fun `keeps the compression number and the fraction of the beat`() {
        // compression 12 (ordinal 11), halfway through the beat
        val frame = 11 * from.spec.beatFrames + from.spec.beatFrames / 2
        val mapped = mapFrameBetween(frame, from, to)
        assertEquals(Position.Compression(12, 30, false), to.positionAt(mapped))
        assertEquals(11 * to.spec.beatFrames + to.spec.beatFrames / 2, mapped)
    }

    @Test
    fun `keeps the fraction of the pause`() {
        val frame = from.pauseStartFrame + from.pauseFrames / 4
        val mapped = mapFrameBetween(frame, from, to)
        assertEquals(to.pauseStartFrame + to.pauseFrames / 4, mapped)
    }

    @Test
    fun `never maps past the end of the target cycle`() {
        val mapped = mapFrameBetween(from.totalFrames - 1, from, to)
        assertEquals(to.totalFrames - 1, mapped)
    }

    @Test
    fun `compressions mode maps within the single beat`() {
        val a = CycleLayout.of(CycleSpec(Mode.COMPRESSIONS, bpm = 100))
        val b = CycleLayout.of(CycleSpec(Mode.COMPRESSIONS, bpm = 120))
        assertEquals(b.totalFrames / 2, mapFrameBetween(a.totalFrames / 2, a, b))
    }
}
