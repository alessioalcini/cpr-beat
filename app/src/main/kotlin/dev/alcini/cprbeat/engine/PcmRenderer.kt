package dev.alcini.cprbeat.engine

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Renders a [CycleLayout] into one cycle of 16-bit mono PCM. The player loops the result, so a
 * tone that runs past the end of the cycle wraps around to its start.
 */
object PcmRenderer {
    private const val PEAK = Short.MAX_VALUE.toDouble()
    private const val FADE_MILLIS = 5

    fun render(layout: CycleLayout): ShortArray {
        val out = ShortArray(layout.totalFrames)
        for (event in layout.events) {
            mix(out, event.frame, synthesize(ToneBank.forEvent(event.kind), layout.spec.sampleRateHz))
        }
        return out
    }

    /** One tone as PCM, envelope applied. */
    fun synthesize(tone: Tone, sampleRateHz: Int): ShortArray {
        val frames = (sampleRateHz * tone.durationMillis / 1000.0).roundToInt()
        val fadeFrames = min(frames / 2, (sampleRateHz * FADE_MILLIS / 1000.0).roundToInt())
        val tau = frames / 4.0 // percussive decay constant: about -35 dB by the end
        val omega = 2 * PI * tone.frequencyHz / sampleRateHz
        val out = ShortArray(frames)
        for (i in 0 until frames) {
            val envelope = if (tone.percussive) {
                exp(-i / tau) * fadeIn(i, fadeFrames)
            } else {
                fadeIn(i, fadeFrames) * fadeIn(frames - 1 - i, fadeFrames)
            }
            out[i] = (sin(omega * i) * envelope * tone.amplitude * PEAK).roundToInt().toShort()
        }
        return out
    }

    private fun fadeIn(i: Int, fadeFrames: Int): Double =
        if (fadeFrames <= 0 || i >= fadeFrames) 1.0 else i / fadeFrames.toDouble()

    private fun mix(into: ShortArray, startFrame: Int, tone: ShortArray) {
        val n = into.size
        for (i in tone.indices) {
            val idx = (startFrame + i) % n
            val sum = into[idx] + tone[i]
            into[idx] = sum.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }
}
