package dev.alcini.cprbeat.session

import dev.alcini.cprbeat.engine.Mode
import dev.alcini.cprbeat.engine.Position
import dev.alcini.cprbeat.engine.Tempo

/** How long the SWITCH RESCUER banner covers the countdown digits after an expiry (SPEC 5.4). */
const val BANNER_MILLIS = 5_000L

/** Everything the main screen shows. Produced by [SessionViewModel]. */
data class SessionState(
    val running: Boolean = false,
    val mode: Mode = Mode.COMPRESSIONS,
    val bpm: Int = Tempo.DEFAULT_BPM,
    val breathPauseMillis: Int = 5_000,
    val countdown: CountdownOption = CountdownOption.DEFAULT,
    /** Milliseconds left on the rescuer-switch countdown; null when it is off. */
    val countdownRemainingMillis: Long? = CountdownOption.DEFAULT.millis,
    /** Milliseconds the SWITCH RESCUER banner stays visible; 0 when hidden. */
    val bannerRemainingMillis: Long = 0,
    /** Increments on every countdown expiry; the UI flashes once per change. */
    val flashCount: Int = 0,
    /** CPR time since START. Frozen after STOP until the next START (handover). */
    val elapsedMillis: Long = 0,
    /** Wall-clock time of START as epoch millis; kept after STOP until the next START. */
    val startedAtEpochMillis: Long? = null,
    /** Alarm volume is low and auto-max is off: show the volume warning. */
    val volumeLow: Boolean = false,
    /** Hints for the first launch are still to be shown. */
    val showHints: Boolean = false,
)

/** What the UI reads at frame rate while running; cheap to produce, not part of the state flow. */
data class BeatSnapshot(val position: Position?, val beatsHeard: Long)
