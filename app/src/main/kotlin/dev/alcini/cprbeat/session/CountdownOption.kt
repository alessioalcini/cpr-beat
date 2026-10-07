package dev.alcini.cprbeat.session

/** Rescuer-switch countdown length. Tapping the countdown on the main screen cycles through these. */
enum class CountdownOption(val minutes: Int) {
    OFF(0), ONE(1), TWO(2), THREE(3), FIVE(5);

    val millis: Long get() = minutes * 60_000L

    fun next(): CountdownOption = entries[(ordinal + 1) % entries.size]

    companion object {
        val DEFAULT = TWO
        fun fromMinutes(minutes: Int): CountdownOption = entries.firstOrNull { it.minutes == minutes } ?: DEFAULT
    }
}
