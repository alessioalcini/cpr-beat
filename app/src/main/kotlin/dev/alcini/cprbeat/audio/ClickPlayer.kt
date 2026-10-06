package dev.alcini.cprbeat.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import dev.alcini.cprbeat.engine.CycleLayout
import dev.alcini.cprbeat.engine.CycleSpec
import dev.alcini.cprbeat.engine.PcmRenderer
import dev.alcini.cprbeat.engine.mapFrameBetween

/**
 * Plays one rendered metronome cycle in a loop. The whole cycle lives in a static AudioTrack
 * buffer with loop points, so timing is decided by the audio hardware clock, not by timers.
 *
 * Audio goes out as an alarm ([AudioAttributes.USAGE_ALARM]): it plays in silent mode and under
 * the default Do Not Disturb rules, like an alarm clock.
 *
 * All methods are blocking and must be called off the main thread. Not thread-safe: the owner
 * serializes calls (the session view model does, on a single-threaded dispatcher).
 */
class ClickPlayer {
    private var track: AudioTrack? = null
    private var layout: CycleLayout? = null

    val current: CycleLayout? get() = layout
    val isPlaying: Boolean get() = track?.playState == AudioTrack.PLAYSTATE_PLAYING

    /** Native output rate of the alarm stream; rendering at it avoids resampling. */
    val nativeSampleRateHz: Int
        get() = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_ALARM).takeIf { it > 0 }
            ?: CycleSpec.DEFAULT_SAMPLE_RATE_HZ

    /** Starts looping the cycle described by [spec] from frame [startFrameInCycle]. */
    fun start(spec: CycleSpec, startFrameInCycle: Int = 0) {
        val newLayout = CycleLayout.of(spec)
        val pcm = PcmRenderer.render(newLayout)
        val newTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(spec.sampleRateHz)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            )
            .setTransferMode(AudioTrack.MODE_STATIC)
            .setBufferSizeInBytes(pcm.size * Short.SIZE_BYTES)
            .build()
        check(newTrack.state == AudioTrack.STATE_NO_STATIC_DATA || newTrack.state == AudioTrack.STATE_INITIALIZED) {
            "AudioTrack failed to initialize (state ${newTrack.state})"
        }
        val written = newTrack.write(pcm, 0, pcm.size)
        check(written == pcm.size) { "AudioTrack.write returned $written of ${pcm.size} frames" }
        check(newTrack.setLoopPoints(0, pcm.size, -1) == AudioTrack.SUCCESS) { "setLoopPoints failed" }
        val start = startFrameInCycle.coerceIn(0, pcm.size - 1)
        if (start > 0) newTrack.setPlaybackHeadPosition(start)

        stop()
        track = newTrack
        layout = newLayout
        newTrack.play()
    }

    /**
     * Switches to a new cycle while keeping the rescuer's place in it: the same compression
     * number and the same fraction of the beat. Used for rate changes. A mode change passes
     * `keepPlace = false` and restarts the new cycle at its first click.
     */
    fun switchTo(spec: CycleSpec, keepPlace: Boolean) {
        val old = layout
        val startFrame = if (keepPlace && old != null && old.spec.mode == spec.mode) {
            mapFrameBetween(frameInCycle(), old, CycleLayout.of(spec))
        } else {
            0
        }
        start(spec, startFrame)
    }

    /** Current playback frame inside the cycle, 0 until totalFrames. 0 when not playing. */
    fun frameInCycle(): Int {
        val t = track ?: return 0
        val l = layout ?: return 0
        val head = t.playbackHeadPosition.toLong() and 0xFFFF_FFFFL
        return (head % l.totalFrames).toInt()
    }

    fun stop() {
        track?.let { t ->
            runCatching { t.pause() }
            runCatching { t.flush() }
            runCatching { t.stop() }
            t.release()
        }
        track = null
        layout = null
    }
}
