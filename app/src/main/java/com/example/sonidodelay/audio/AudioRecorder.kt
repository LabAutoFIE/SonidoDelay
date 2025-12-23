package com.example.sonidodelay.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

class AudioRecorder(
    private val sampleRate: Int = 44100,
    private val frameSize: Int = 1024
) {
    private val minBuffer = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )
    private val bufferSize = maxOf(minBuffer, frameSize * 4)

    private val audioRecord = AudioRecord(
        MediaRecorder.AudioSource.VOICE_COMMUNICATION, // menor latencia en varios dispositivos
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufferSize
    )

    @Volatile
    var isRecording = false
        private set

    fun start(onData: (ShortArray) -> Unit) {
        if (isRecording) return
        isRecording = true
        audioRecord.startRecording()
        Thread {
            val buffer = ShortArray(frameSize)
            while (isRecording) {
                val read = audioRecord.read(buffer, 0, buffer.size)
                if (read > 0) onData(buffer.copyOf(read))
            }
        }.start()
    }

    fun stop() {
        isRecording = false
        try {
            audioRecord.stop()
        } finally {
            audioRecord.release()
        }
    }
}

