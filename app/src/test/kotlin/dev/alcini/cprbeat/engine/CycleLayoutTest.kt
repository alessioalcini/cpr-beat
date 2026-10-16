package dev.alcini.cprbeat.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CycleLayoutTest {
    private val sr = 48_000
    private val beat110 = 26_182 // 48000 * 60 / 110, rounded

    @Test
    fun `compressions mode is one beat with a single click at frame zero`() {
        val layout = CycleLayout.of(CycleSpec(Mode.COMPRESSIONS, bpm = 110))
        assertEquals(beat110, layout.totalFrames)
        assertEquals(beat110, layout.pauseStartFrame)
        assertEquals(0, layout.pauseFrames)
        assertEquals(listOf(CycleEvent(EventKind.CLICK, 0, 1)), layout.events)
    }

    @Test
    fun `thirty-two cycle is thirty beats plus the pause`() {
        val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 110, breathPauseMillis = 5_000))
        assertEquals(30 * beat110, layout.pauseStartFrame)
        assertEquals(5 * sr, layout.pauseFrames)
        assertEquals(30 * beat110 + 5 * sr, layout.totalFrames)
    }

    @Test
    fun `clicks land one beat apart and the last five are warnings`() {
        val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 110))
        val clicks = layout.events.filter { it.kind == EventKind.CLICK || it.kind == EventKind.WARNING_CLICK }
        assertEquals(30, clicks.size)
        clicks.forEachIndexed { i, e ->
            assertEquals(i * beat110, e.frame)
            assertEquals(i + 1, e.compression)
            val expected = if (i + 1 >= 26) EventKind.WARNING_CLICK else EventKind.CLICK
            assertEquals("compression ${i + 1}", expected, e.kind)
        }
    }

    @Test
    fun `breath tones sound at 30 and 65 percent of the pause and the resume tone precedes its end`() {
        for (pause in 3_000..9_000 step 1_000) {
            val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 100, breathPauseMillis = pause))
            val pauseStart = layout.pauseStartFrame
            val expected = listOf(pauseStart + pause * 48 * 30 / 100, pauseStart + pause * 48 * 65 / 100)
            val breaths = layout.events.filter { it.kind == EventKind.BREATH_TONE }.map { it.frame }
            val resume = layout.events.single { it.kind == EventKind.RESUME_TONE }.frame
            assertEquals("pause $pause", expected, breaths)
            assertEquals(expected, listOf(layout.breathFrame(1), layout.breathFrame(2)))
            assertEquals(layout.totalFrames - sr / 2, resume)
            assertTrue(pauseStart < breaths[0] && breaths[0] < breaths[1] && breaths[1] < resume && resume < layout.totalFrames)
        }
    }

    @Test
    fun `default pause gives a lone rescuer over two seconds before the first breath tone`() {
        val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 110))
        assertEquals(7 * sr, layout.pauseFrames)
        assertEquals(layout.pauseStartFrame + 2_100 * 48, layout.breathFrame(1))
        assertEquals(layout.pauseStartFrame + 4_550 * 48, layout.breathFrame(2))
    }

    @Test
    fun `position reports the compression number and the warning zone`() {
        val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 110))
        assertEquals(Position.Compression(1, 30, false), layout.positionAt(0))
        assertEquals(Position.Compression(1, 30, false), layout.positionAt(beat110 - 1))
        assertEquals(Position.Compression(2, 30, false), layout.positionAt(beat110))
        assertEquals(Position.Compression(25, 30, false), layout.positionAt(24 * beat110))
        assertEquals(Position.Compression(26, 30, true), layout.positionAt(25 * beat110))
        assertEquals(Position.Compression(30, 30, true), layout.positionAt(30 * beat110 - 1))
    }

    @Test
    fun `position inside the pause counts cued breaths`() {
        val spec = CycleSpec(Mode.THIRTY_TWO, bpm = 110, breathPauseMillis = 5_000)
        val layout = CycleLayout.of(spec)
        val p0 = layout.positionAt(layout.pauseStartFrame)
        assertEquals(Position.BreathPause(0, 5 * sr, 0), p0)
        val first = layout.pauseStartFrame + 5 * sr * 30 / 100
        val second = layout.pauseStartFrame + 5 * sr * 65 / 100
        assertEquals(0, (layout.positionAt(first - 1) as Position.BreathPause).breathsCued)
        assertEquals(1, (layout.positionAt(first) as Position.BreathPause).breathsCued)
        assertEquals(1, (layout.positionAt(second - 1) as Position.BreathPause).breathsCued)
        assertEquals(2, (layout.positionAt(second) as Position.BreathPause).breathsCued)
        val last = layout.positionAt(layout.totalFrames - 1) as Position.BreathPause
        assertEquals(5 * sr - 1, last.elapsedFrames)
    }

    @Test
    fun `compressions mode position is always compression one of one`() {
        val layout = CycleLayout.of(CycleSpec(Mode.COMPRESSIONS, bpm = 120))
        assertEquals(Position.Compression(1, 1, false), layout.positionAt(0))
        assertEquals(Position.Compression(1, 1, false), layout.positionAt(layout.totalFrames - 1))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `position outside the cycle is rejected`() {
        val layout = CycleLayout.of(CycleSpec(Mode.COMPRESSIONS, bpm = 110))
        layout.positionAt(layout.totalFrames)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `bpm outside the supported range is rejected`() {
        CycleSpec(Mode.COMPRESSIONS, bpm = 90)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `breath pause above the supported range is rejected`() {
        CycleSpec(Mode.THIRTY_TWO, bpm = 110, breathPauseMillis = 10_000)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `breath pause below the supported range is rejected`() {
        CycleSpec(Mode.THIRTY_TWO, bpm = 110, breathPauseMillis = 2_000)
    }

    @Test
    fun `cycle lengths for every supported tempo and pause stay within the static buffer budget`() {
        for (bpm in listOf(100, 110, 120)) for (pause in 3_000..9_000 step 1_000) {
            val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm, pause))
            val bytes = layout.totalFrames * 2L
            assertTrue("bpm=$bpm pause=$pause bytes=$bytes", bytes < 3_000_000L)
            assertEquals(30 * Tempo.framesPerBeat(bpm, sr) + pause * 48, layout.totalFrames)
        }
    }
}
