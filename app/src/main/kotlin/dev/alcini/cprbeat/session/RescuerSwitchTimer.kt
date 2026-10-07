package dev.alcini.cprbeat.session

/**
 * The rescuer-switch countdown. Pure logic on a monotonic clock passed in by the caller
 * (SystemClock.elapsedRealtime in the app, a fake in tests). Restarts itself on expiry and
 * reports how many expiries happened since the last call, so the UI can flash and show the
 * SWITCH RESCUER banner even if it was not polled exactly at zero.
 */
class RescuerSwitchTimer(option: CountdownOption, nowMillis: Long) {
    var option: CountdownOption = option
        private set
    private var endMillis: Long = nowMillis + option.millis

    /** Milliseconds left in the current countdown; null when the countdown is off. */
    fun remaining(nowMillis: Long): Long? =
        if (option == CountdownOption.OFF) null else (endMillis - nowMillis).coerceAtLeast(0)

    /** Advances the timer; returns the number of expiries since the previous call (normally 0 or 1). */
    fun tick(nowMillis: Long): Int {
        if (option == CountdownOption.OFF) return 0
        var expiries = 0
        while (nowMillis >= endMillis) {
            endMillis += option.millis
            expiries++
        }
        return expiries
    }

    /** Switches to [newOption] and restarts the countdown from now (per-session override). */
    fun restart(newOption: CountdownOption, nowMillis: Long) {
        option = newOption
        endMillis = nowMillis + newOption.millis
    }
}
