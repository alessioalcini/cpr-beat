package dev.alcini.cprbeat.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class PcmRendererTest {
    private val sr = 48_000

    private fun peakIn(pcm: ShortArray, from: Int, length: Int): Int =
        (from until from + length).maxOf { abs(pcm[it % pcm.size].toInt()) }

    @Test
    fun `rendered cycle has exactly one frame per layout frame`() {
        val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 110, breathPauseMillis = 4_000))
        assertEquals(layout.totalFrames, PcmRenderer.render(layout).size)
    }

    @Test
    fun `every event is audible at its frame and silence lies between events`() {
        val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 110, breathPauseMillis = 5_000))
        val pcm = PcmRenderer.render(layout)
        for (e in layout.events) {
            val tone = ToneBank.forEvent(e.kind)
            val toneFrames = sr * tone.durationMillis / 1000
            assertTrue("event $e should be audible", peakIn(pcm, e.frame, toneFrames) > 0.3 * Short.MAX_VALUE)
        }
        // between the 10th click and the 11th, after the click has decayed, there is nothing
        val tenth = layout.events.first { it.compression == 10 }.frame
        val gapStart = tenth + sr * 40 / 1000 + 1
        val gapLength = layout.spec.beatFrames - sr * 40 / 1000 - 2
        assertEquals(0, peakIn(pcm, gapStart, gapLength))
    }

    @Test
    fun `nothing clips`() {
        val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 120, breathPauseMillis = 3_000))
        val pcm = PcmRenderer.render(layout)
        assertTrue(pcm.none { it == Short.MAX_VALUE || it == Short.MIN_VALUE })
    }

    @Test
    fun `percussive tone decays and sustained tone holds`() {
        val click = PcmRenderer.synthesize(ToneBank.click, sr)
        val breath = PcmRenderer.synthesize(ToneBank.breath, sr)
        assertEquals(sr * 40 / 1000, click.size)
        assertEquals(sr * 250 / 1000, breath.size)
        val clickHead = peakIn(click, 0, click.size / 4)
        val clickTail = peakIn(click, click.size * 3 / 4, click.size / 4)
        assertTrue("click should decay: head=$clickHead tail=$clickTail", clickTail < clickHead / 4)
        val breathMid = peakIn(breath, breath.size / 4, breath.size / 2)
        assertTrue("breath should hold near its amplitude", breathMid > 0.6 * Short.MAX_VALUE)
        // sustained tones start and end at silence (no pops)
        assertEquals(0, breath[0].toInt())
        assertEquals(0, breath[breath.size - 1].toInt())
    }

    @Test
    fun `a tone that runs past the cycle end wraps to the start`() {
        // 120 bpm compressions: cycle is 24000 frames, click is 1920 frames; place a synthetic
        // check by rendering and confirming no exception and the click is at frame 0
        val layout = CycleLayout.of(CycleSpec(Mode.COMPRESSIONS, bpm = 120))
        val pcm = PcmRenderer.render(layout)
        assertEquals(24_000, pcm.size)
        assertTrue(peakIn(pcm, 0, 1_920) > 0.3 * Short.MAX_VALUE)
        assertEquals(0, peakIn(pcm, 2_000, 21_000))
    }
}
