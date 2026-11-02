package com.example.tilefall.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundHapticsManager(context: Context) {

    private val appContext = context.applicationContext
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val audioScope = CoroutineScope(Dispatchers.Default)

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    // Pre-synthesized PCM buffers
    private val tapAudioData by lazy { generateSweepPcm(startFreq = 580f, endFreq = 880f, durationMs = 40) }
    private val streakAudioData by lazy { generateSweepPcm(startFreq = 660f, endFreq = 990f, durationMs = 70) }
    private val missAudioData by lazy { generateSweepPcm(startFreq = 220f, endFreq = 120f, durationMs = 90) }
    private val gameOverAudioData by lazy { generateSweepPcm(startFreq = 260f, endFreq = 90f, durationMs = 220) }

    private fun generateSweepPcm(startFreq: Float, endFreq: Float, durationMs: Int): ShortArray {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val buffer = ShortArray(numSamples)
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            // Linear frequency sweep
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            val phaseIncrement = 2.0 * PI * currentFreq / sampleRate
            phase += phaseIncrement

            // Smooth envelope (linear attack, exponential decay)
            val envelope = if (progress < 0.15) {
                progress / 0.15
            } else {
                (1.0 - progress) * (1.0 - progress)
            }

            val sample = (sin(phase) * envelope * 24000.0).toInt().coerceIn(-32768, 32767)
            buffer[i] = sample.toShort()
        }
        return buffer
    }

    private fun playPcm(pcmData: ShortArray) {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                val sampleRate = 44100
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(pcmData.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcmData, 0, pcmData.size)
                track.play()

                // Auto-release after playing
                val durationMs = (pcmData.size * 1000L) / sampleRate + 50L
                kotlinx.coroutines.delay(durationMs)
                track.stop()
                track.release()
            } catch (_: Exception) {
                // Ignore audio failure on restricted platforms
            }
        }
    }

    fun playTap() {
        playPcm(tapAudioData)
        triggerHapticClick()
    }

    fun playStreak() {
        playPcm(streakAudioData)
        triggerHapticMilestone()
    }

    fun playMiss() {
        playPcm(missAudioData)
        triggerHapticMiss()
    }

    fun playGameOver() {
        playPcm(gameOverAudioData)
        triggerHapticGameOver()
    }

    private fun triggerHapticClick() {
        if (!isHapticsEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(18, 120))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(18)
            }
        } catch (_: Exception) {
        }
    }

    private fun triggerHapticMilestone() {
        if (!isHapticsEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 20, 30, 30),
                        intArrayOf(0, 160, 0, 200),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 20, 30, 30), -1)
            }
        } catch (_: Exception) {
        }
    }

    private fun triggerHapticMiss() {
        if (!isHapticsEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 50, 40, 50),
                        intArrayOf(0, 220, 0, 240),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 50, 40, 50), -1)
            }
        } catch (_: Exception) {
        }
    }

    private fun triggerHapticGameOver() {
        if (!isHapticsEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(180, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(180)
            }
        } catch (_: Exception) {
        }
    }
}
