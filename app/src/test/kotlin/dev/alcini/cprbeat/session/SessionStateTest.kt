package dev.alcini.cprbeat.session

import dev.alcini.cprbeat.engine.Mode
import dev.alcini.cprbeat.settings.Settings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionStateTest {
    private val settings = Settings(defaultBpm = 100, defaultCountdown = CountdownOption.THREE)

    @Test
    fun `stop drops the main-screen overrides and goes back to the settings`() {
        val overridden = SessionState(
            mode = Mode.THIRTY_TWO,
            bpm = 120,
            countdown = CountdownOption.FIVE,
            countdownRemainingMillis = 42_000,
        )
        val after = overridden.withDefaultsFrom(settings)
        assertEquals(Mode.COMPRESSIONS, after.mode)
        assertEquals(100, after.bpm)
        assertEquals(CountdownOption.THREE, after.countdown)
        assertEquals(180_000L, after.countdownRemainingMillis)
    }

    @Test
    fun `countdown off in the settings shows no remaining time`() {
        val after = SessionState().withDefaultsFrom(settings.copy(defaultCountdown = CountdownOption.OFF))
        assertEquals(CountdownOption.OFF, after.countdown)
        assertNull(after.countdownRemainingMillis)
    }

    @Test
    fun `handover time survives the reset`() {
        val stopped = SessionState(elapsedMillis = 252_000, startedAtEpochMillis = 1_000L, mode = Mode.THIRTY_TWO)
        val after = stopped.withDefaultsFrom(settings)
        assertEquals(252_000L, after.elapsedMillis)
        assertEquals(1_000L, after.startedAtEpochMillis)
    }
}
