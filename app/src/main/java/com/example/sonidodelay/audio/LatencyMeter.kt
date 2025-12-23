package com.example.sonidodelay.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.media.AudioAttributes
import kotlin.system.measureTimeMillis

class LatencyMeter(private val sampleRate: Int = 44100) {

    fun measureLatency(): Long {
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .build()

        val pulse = ShortArray(256) { if (it == 0) Short.MAX_VALUE else 0 }

        recorder.startRecording()
        track.play()

        var latencyMs: Long = -1

        val time = measureTimeMillis {
            track.write(pulse, 0, pulse.size)

            val buffer = ShortArray(256)
            var detected = false
            while (!detected) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0 && buffer.any { it > 10000 }) {
                    detected = true
                }
            }
        }

        latencyMs = time

        recorder.stop()
        recorder.release()
        track.stop()
        track.release()

        return latencyMs
    }
}