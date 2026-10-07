package dev.alcini.cprbeat.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class PcmRendererTest {
    private val sr = 48_000
    private val bank = ToneBank.DEFAULT

    private fun peakIn(pcm: ShortArray, from: Int, length: Int): Int =
        (from until from + length).maxOf { abs(pcm[it % pcm.size].toInt()) }

    private fun frames(millis: Int) = sr * millis / 1000

    @Test
    fun `rendered cycle has exactly one frame per layout frame`() {
        val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 110, breathPauseMillis = 4_000))
        assertEquals(layout.totalFrames, PcmRenderer.render(layout).size)
    }

    @Test
    fun `every event is audible at its frame and silence lies between events`() {
        val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 110, breathPauseMillis = 5_000))
        val pcm = PcmRenderer.render(layout, bank)
        for (e in layout.events) {
            val tone = bank.forEvent(e.kind)
            assertTrue("event $e should be audible", peakIn(pcm, e.frame, frames(tone.durationMillis)) > 0.3 * Short.MAX_VALUE)
        }
        val tenth = layout.events.first { it.compression == 10 }.frame
        val clickFrames = frames(bank.click.durationMillis)
        assertEquals(0, peakIn(pcm, tenth + clickFrames + 1, layout.spec.beatFrames - clickFrames - 2))
    }

    @Test
    fun `nothing clips in any preset`() {
        for (preset in ToneBank.presets) {
            val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 120, breathPauseMillis = 3_000))
            val pcm = PcmRenderer.render(layout, preset)
            assertTrue(preset.name, pcm.none { it == Short.MAX_VALUE || it == Short.MIN_VALUE })
        }
    }

    @Test
    fun `peak equals the declared amplitude for every tone of every preset`() {
        for (preset in ToneBank.presets) for (kind in EventKind.entries) {
            val tone = preset.forEvent(kind)
            val pcm = PcmRenderer.synthesize(tone, sr)
            val peak = pcm.maxOf { abs(it.toInt()) }
            val expected = (tone.amplitude * Short.MAX_VALUE).toInt()
            assertTrue("${preset.name}/$kind peak $peak vs $expected", abs(peak - expected) <= 1)
        }
    }

    @Test
    fun `click is never quieter than the other cues`() {
        for (preset in ToneBank.presets) {
            val click = PcmRenderer.synthesize(preset.click, sr).maxOf { abs(it.toInt()) }
            for (kind in listOf(EventKind.BREATH_TONE, EventKind.RESUME_TONE)) {
                val other = PcmRenderer.synthesize(preset.forEvent(kind), sr).maxOf { abs(it.toInt()) }
                assertTrue("${preset.name}: click $click < $kind $other", click >= other)
            }
        }
    }

    @Test
    fun `percussive tone decays and every tone ends at silence`() {
        val click = PcmRenderer.synthesize(bank.click, sr)
        assertEquals(frames(bank.click.durationMillis), click.size)
        val head = (0 until click.size / 4).maxOf { abs(click[it].toInt()) }
        val tail = (click.size * 3 / 4 until click.size).maxOf { abs(click[it].toInt()) }
        assertTrue("click should decay: head=$head tail=$tail", tail < head / 4)
        for (preset in ToneBank.presets) for (kind in EventKind.entries) {
            val pcm = PcmRenderer.synthesize(preset.forEvent(kind), sr)
            assertEquals("${preset.name}/$kind must end at zero", 0, pcm.last().toInt())
            assertEquals("${preset.name}/$kind must start at zero", 0, pcm.first().toInt())
        }
    }

    @Test
    fun `sustained tone holds near its amplitude`() {
        val breath = PcmRenderer.synthesize(bank.breath, sr)
        assertEquals(frames(bank.breath.durationMillis), breath.size)
        val mid = (breath.size / 4 until breath.size * 3 / 4).maxOf { abs(breath[it].toInt()) }
        assertTrue("breath mid peak $mid", mid > 0.9 * bank.breath.amplitude * Short.MAX_VALUE)
    }

    @Test
    fun `breath cues and the resume tone never overlap even in the shortest pause`() {
        for (bpm in listOf(100, 110, 120)) {
            val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm, breathPauseMillis = 3_000))
            val sounded = layout.events.map { e -> e.frame to e.frame + frames(bank.forEvent(e.kind).durationMillis) }
            val sorted = sounded.sortedBy { it.first }
            for (i in 1 until sorted.size) {
                assertTrue("events overlap at bpm $bpm: ${sorted[i - 1]} vs ${sorted[i]}", sorted[i].first >= sorted[i - 1].second)
            }
            assertTrue(sorted.last().second <= layout.totalFrames)
        }
    }

    @Test
    fun `a tone that runs past the cycle end wraps to the start`() {
        val layout = CycleLayout.of(CycleSpec(Mode.COMPRESSIONS, bpm = 120))
        val longTone = ToneBank.DEFAULT.copy(click = Tone(880.0, durationMillis = 600, amplitude = 0.5, decayMillis = 100.0))
        val pcm = PcmRenderer.render(layout, longTone)
        assertEquals(24_000, pcm.size)
        // 600 ms = 28800 frames into a 24000-frame cycle: the last 4800 frames wrap to the start and mix with the head
        assertTrue(peakIn(pcm, 0, 100) > 0)
        assertTrue(peakIn(pcm, 23_000, 1_000) > 0)
    }
}
