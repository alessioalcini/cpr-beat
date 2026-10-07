package dev.alcini.cprbeat.engine

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Renders a [CycleLayout] into one cycle of 16-bit mono PCM. The player loops the result, so a
 * tone that runs past the end of the cycle wraps around to its start.
 */
object PcmRenderer {
    private const val PEAK = Short.MAX_VALUE.toDouble()

    fun render(layout: CycleLayout, bank: ToneBank = ToneBank.DEFAULT): ShortArray {
        val out = ShortArray(layout.totalFrames)
        for (event in layout.events) {
            mix(out, event.frame, synthesize(bank.forEvent(event.kind), layout.spec.sampleRateHz))
        }
        return out
    }

    /**
     * One tone as PCM. Partials are summed, the envelope applied, then the whole thing is scaled
     * so that its true peak equals [Tone.amplitude] of full scale.
     */
    fun synthesize(tone: Tone, sampleRateHz: Int): ShortArray {
        val frames = framesOf(tone.durationMillis.toDouble(), sampleRateHz)
        val attack = framesOf(tone.attackMillis, sampleRateHz).coerceAtMost(frames / 2)
        val release = framesOf(tone.releaseMillis, sampleRateHz).coerceAtMost(frames / 2)
        val tau = tone.decayMillis?.let { it * sampleRateHz / 1000.0 }
        val omega = 2 * PI * tone.frequencyHz / sampleRateHz
        val raw = DoubleArray(frames)
        var peak = 0.0
        for (i in 0 until frames) {
            var sample = 0.0
            for (p in tone.partials) sample += p.level * sin(omega * p.harmonic * i)
            var envelope = 1.0
            if (attack > 0 && i < attack) envelope *= i / attack.toDouble()
            if (tau != null) envelope *= exp(-i / tau)
            val remaining = frames - 1 - i
            if (release > 0 && remaining < release) envelope *= remaining / release.toDouble()
            raw[i] = sample * envelope
            if (abs(raw[i]) > peak) peak = abs(raw[i])
        }
        val scale = if (peak > 0) tone.amplitude * PEAK / peak else 0.0
        return ShortArray(frames) { (raw[it] * scale).roundToInt().toShort() }
    }

    private fun framesOf(millis: Double, sampleRateHz: Int): Int = (sampleRateHz * millis / 1000.0).roundToInt()

    private fun mix(into: ShortArray, startFrame: Int, tone: ShortArray) {
        val n = into.size
        for (i in tone.indices) {
            val idx = (startFrame + i) % n
            val sum = into[idx] + tone[i]
            into[idx] = sum.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }
}
