package dev.alcini.cprbeat.session

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RescuerSwitchTimerTest {
    @Test
    fun `counts down and restarts itself on expiry`() {
        val t = RescuerSwitchTimer(CountdownOption.TWO, nowMillis = 1_000)
        assertEquals(120_000L, t.remaining(1_000))
        assertEquals(0, t.tick(60_000))
        assertEquals(61_000L, t.remaining(60_000))
        assertEquals(1, t.tick(121_000))
        assertEquals(120_000L, t.remaining(121_000))
        assertEquals(0, t.tick(121_100))
    }

    @Test
    fun `a long stall reports every missed expiry and lands on the right phase`() {
        val t = RescuerSwitchTimer(CountdownOption.ONE, nowMillis = 0)
        assertEquals(3, t.tick(185_000))
        assertEquals(55_000L, t.remaining(185_000))
    }

    @Test
    fun `off never expires and has no remaining time`() {
        val t = RescuerSwitchTimer(CountdownOption.OFF, nowMillis = 0)
        assertNull(t.remaining(0))
        assertEquals(0, t.tick(10_000_000))
    }

    @Test
    fun `restart switches the option and starts from now`() {
        val t = RescuerSwitchTimer(CountdownOption.TWO, nowMillis = 0)
        t.tick(50_000)
        t.restart(CountdownOption.FIVE, nowMillis = 50_000)
        assertEquals(CountdownOption.FIVE, t.option)
        assertEquals(300_000L, t.remaining(50_000))
        t.restart(CountdownOption.OFF, nowMillis = 60_000)
        assertNull(t.remaining(60_000))
    }

    @Test
    fun `remaining never goes negative`() {
        val t = RescuerSwitchTimer(CountdownOption.ONE, nowMillis = 0)
        assertEquals(0L, t.remaining(61_000))
    }
}
