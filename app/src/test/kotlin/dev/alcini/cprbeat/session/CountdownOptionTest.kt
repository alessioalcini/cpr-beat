package dev.alcini.cprbeat.session

import org.junit.Assert.assertEquals
import org.junit.Test

class CountdownOptionTest {
    @Test
    fun `tap cycles off one two three five and back to off`() {
        var o = CountdownOption.OFF
        val seen = mutableListOf<CountdownOption>()
        repeat(6) { seen += o; o = o.next() }
        assertEquals(listOf(CountdownOption.OFF, CountdownOption.ONE, CountdownOption.TWO, CountdownOption.THREE, CountdownOption.FIVE, CountdownOption.OFF), seen)
    }

    @Test
    fun `unknown stored minutes fall back to the default`() {
        assertEquals(CountdownOption.THREE, CountdownOption.fromMinutes(3))
        assertEquals(CountdownOption.DEFAULT, CountdownOption.fromMinutes(4))
        assertEquals(0L, CountdownOption.OFF.millis)
        assertEquals(300_000L, CountdownOption.FIVE.millis)
    }
}
