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
    fun `breath and resume tones sit inside the pause at the specified offsets`() {
        val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm = 100, breathPauseMillis = 3_000))
        val pauseStart = layout.pauseStartFrame
        val breaths = layout.events.filter { it.kind == EventKind.BREATH_TONE }.map { it.frame }
        val resume = layout.events.single { it.kind == EventKind.RESUME_TONE }.frame
        assertEquals(listOf(pauseStart + sr / 2, pauseStart + 2 * sr), breaths)
        assertEquals(layout.totalFrames - sr / 2, resume)
        // the shortest allowed pause still orders the cues: breath 1 < breath 2 < resume < end
        assertTrue(breaths[0] < breaths[1] && breaths[1] < resume && resume < layout.totalFrames)
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
        val p1 = layout.positionAt(layout.pauseStartFrame + sr / 2)
        assertEquals(1, (p1 as Position.BreathPause).breathsCued)
        val p2 = layout.positionAt(layout.pauseStartFrame + 2 * sr)
        assertEquals(2, (p2 as Position.BreathPause).breathsCued)
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
    fun `breath pause outside the supported range is rejected`() {
        CycleSpec(Mode.THIRTY_TWO, bpm = 110, breathPauseMillis = 9_000)
    }

    @Test
    fun `cycle lengths for every supported tempo and pause stay within the static buffer budget`() {
        for (bpm in listOf(100, 110, 120)) for (pause in 3_000..8_000 step 1_000) {
            val layout = CycleLayout.of(CycleSpec(Mode.THIRTY_TWO, bpm, pause))
            val bytes = layout.totalFrames * 2L
            assertTrue("bpm=$bpm pause=$pause bytes=$bytes", bytes < 3_000_000L)
            assertEquals(30 * Tempo.framesPerBeat(bpm, sr) + pause * 48, layout.totalFrames)
        }
    }
}
