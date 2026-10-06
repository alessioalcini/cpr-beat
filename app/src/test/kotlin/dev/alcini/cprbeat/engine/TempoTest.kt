package dev.alcini.cprbeat.engine

import org.junit.Assert.assertEquals
import org.junit.Test

class TempoTest {
    @Test
    fun `110 bpm is about 545 ms per beat`() {
        assertEquals(545.4545, Tempo.periodMillis(110), 0.001)
    }

    @Test
    fun `100 bpm is exactly 600 ms per beat`() {
        assertEquals(600.0, Tempo.periodMillis(100), 0.0)
    }

    @Test
    fun `frames per beat at 48 kHz are rounded to the nearest frame`() {
        // 48000 * 60 / 110 = 26181.8...
        assertEquals(26182, Tempo.framesPerBeat(110, 48_000))
        assertEquals(28_800, Tempo.framesPerBeat(100, 48_000))
        assertEquals(24_000, Tempo.framesPerBeat(120, 48_000))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero bpm is rejected`() {
        Tempo.periodMillis(0)
    }
}
