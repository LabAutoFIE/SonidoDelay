package com.example.sonidodelay.audio

import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.max

class AudioDelayBuffer(
    delayMs: Int,
    private val sampleRate: Int,
    private val frameSize: Int
) {
    private val queue = LinkedBlockingQueue<ShortArray>()
    private val delaySamplesTarget = max(0, (sampleRate * delayMs) / 1000)
    @Volatile private var accumulatedSamples = 0

    fun clear() {
        queue.clear()
        accumulatedSamples = 0
    }

    fun add(frame: ShortArray) {
        queue.offer(frame)
        accumulatedSamples += frame.size
    }

    fun pollForPlayback(): ShortArray? {
        // Espera hasta tener suficientes muestras acumuladas para cumplir el retardo
        if (accumulatedSamples < delaySamplesTarget) return null
        val out = queue.poll()
        if (out != null) accumulatedSamples -= out.size
        return out
    }
}

