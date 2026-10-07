package dev.alcini.cprbeat.session

import dev.alcini.cprbeat.engine.CycleLayout
import dev.alcini.cprbeat.engine.CycleSpec
import dev.alcini.cprbeat.engine.Mode
import org.junit.Assert.assertEquals
import org.junit.Test

class BeatDetectorTest {
    private val comp = CycleLayout.of(CycleSpec(Mode.COMPRESSIONS, bpm = 110))
    private val thirtyTwo = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 110, breathPauseMillis = 5_000))
    private val beat = comp.spec.beatFrames

    @Test
    fun `compressions mode reports one click per cycle wrap`() {
        val d = BeatDetector()
        assertEquals(0, d.feed(comp, 0))
        assertEquals(0, d.feed(comp, beat / 2))
        assertEquals(0, d.feed(comp, beat - 1))
        assertEquals(1, d.feed(comp, 10))        // wrapped: click 2
        assertEquals(0, d.feed(comp, beat - 100))
        assertEquals(1, d.feed(comp, 5))         // click 3
        assertEquals(2L, d.beatsHeard)
    }

    @Test
    fun `thirty-two mode reports one click per compression and none in the pause`() {
        val d = BeatDetector()
        d.feed(thirtyTwo, 0)
        assertEquals(1, d.feed(thirtyTwo, beat + 1))
        assertEquals(1, d.feed(thirtyTwo, 2 * beat + 1))
        assertEquals(0, d.feed(thirtyTwo, 2 * beat + beat / 2))
        d.feed(thirtyTwo, 29 * beat + 1)
        assertEquals(0, d.feed(thirtyTwo, thirtyTwo.pauseStartFrame + 1_000))
        assertEquals(0, d.feed(thirtyTwo, thirtyTwo.totalFrames - 1))
        assertEquals(1, d.feed(thirtyTwo, 5))    // first click of the next cycle
        assertEquals(30L, d.beatsHeard)
    }

    @Test
    fun `a stall across several beats reports all of them`() {
        val d = BeatDetector()
        d.feed(thirtyTwo, 0)
        assertEquals(4, d.feed(thirtyTwo, 4 * beat + 10))
    }

    @Test
    fun `a layout switch resets the grid without inventing clicks`() {
        val d = BeatDetector()
        d.feed(comp, 0)
        d.feed(comp, 10)
        assertEquals(0, d.feed(thirtyTwo, beat / 2))
        assertEquals(1, d.feed(thirtyTwo, beat + 10))
        assertEquals(1L, d.beatsHeard)
    }

    @Test
    fun `reset clears the count`() {
        val d = BeatDetector()
        d.feed(comp, 0); d.feed(comp, 1)
        d.feed(comp, 0)
        d.reset()
        assertEquals(0L, d.beatsHeard)
    }
}
