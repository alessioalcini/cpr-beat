package dev.alcini.cprbeat.ui

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** mm:ss, or h:mm:ss once an hour has passed. */
fun formatElapsed(millis: Long): String {
    val total = millis / 1000
    val h = total / 3600
    val m = (total % 3600) / 60
    val s = total % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}

/** mm:ss for the countdown; rounds up so 00:00 shows only at expiry. */
fun formatCountdown(millis: Long): String {
    val total = (millis + 999) / 1000
    return "%02d:%02d".format(total / 60, total % 60)
}

private val clockFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun formatClock(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).format(clockFormat)
