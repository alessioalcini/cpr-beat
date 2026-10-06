package dev.alcini.cprbeat.audio

import android.content.Context
import android.media.AudioManager

/**
 * Raises the alarm stream to maximum for a session when it is below [THRESHOLD] of the maximum,
 * and restores the previous level afterwards. Nothing happens when the volume is already loud.
 */
class VolumeController(context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var previousIndex: Int? = null

    val stream: Int get() = AudioManager.STREAM_ALARM

    /** Current alarm volume as a fraction 0..1. */
    fun level(): Float {
        val max = audioManager.getStreamMaxVolume(stream).coerceAtLeast(1)
        return audioManager.getStreamVolume(stream).toFloat() / max
    }

    /** Called on START when the auto-max setting is on. Returns true if the volume was raised. */
    fun raiseIfQuiet(): Boolean {
        val max = audioManager.getStreamMaxVolume(stream)
        val current = audioManager.getStreamVolume(stream)
        if (current >= max * THRESHOLD) return false
        return runCatching {
            audioManager.setStreamVolume(stream, max, 0)
            previousIndex = current
            true
        }.getOrDefault(false) // a Do Not Disturb policy may forbid changing the volume
    }

    /** Called on STOP: puts the volume back where the user had it. */
    fun restore() {
        val previous = previousIndex ?: return
        previousIndex = null
        runCatching { audioManager.setStreamVolume(stream, previous, 0) }
    }

    companion object {
        const val THRESHOLD = 0.7f
    }
}
