package com.example.sonidodelay.ui.theme

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sonidodelay.R
import com.example.sonidodelay.audio.AudioDelayBuffer
import com.example.sonidodelay.audio.AudioPlayer
import com.example.sonidodelay.audio.AudioRecorder
import com.example.sonidodelay.audio.LatencyMeter
import com.example.sonidodelay.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recorder: AudioRecorder
    private lateinit var player: AudioPlayer
    private lateinit var delayBuffer: AudioDelayBuffer

    private var delayMs = 200
    private val sampleRate = 44100
    private val frameSize = 1024

    private val requestRecordAudio = 200

    // Nuevo launcher para pedir permiso
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                initAudio()
                Toast.makeText(this, "Permiso de micrófono concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Se necesita acceso al micrófono", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        checkMicrophonePermission()
        initAudio()
    }

    private fun initAudio() {
        delayBuffer = AudioDelayBuffer(delayMs, sampleRate, frameSize)
        recorder = AudioRecorder(sampleRate, frameSize)
        player = AudioPlayer(sampleRate, frameSize)

        recorder.start { frame ->
            delayBuffer.add(frame)
        }

        player.start()

        Thread {
            while (!isFinishing) {
                val frame = delayBuffer.pollForPlayback()
                if (frame != null) player.play(frame)
                else Thread.sleep(2) // espera breve si aún no hay suficiente retardo
            }
        }.start()
    }

    private fun setupUI() {
        binding.seekBar.max = 500
        binding.seekBar.progress = delayMs
        binding.textDelay.text = getString(R.string.retardo_label2, delayMs)

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, value: Int, fromUser: Boolean) {
                val newDelay = value.coerceAtLeast(50)
                delayMs = newDelay
                binding.textDelay.text = getString(R.string.retardo_label2, delayMs)
                // Reinicia Buffer p/ aplicar nuevo retardo
                if (::delayBuffer.isInitialized) delayBuffer.clear()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        // Botón p/ medir latencia
        binding.buttonMeasure.setOnClickListener {
            Thread {
                val meter = LatencyMeter(sampleRate)
                val latency = meter.measureLatency()
                runOnUiThread {
                    binding.textHint.text = getString(R.string.latencia_medida, latency)
                }
            }.start()
        }
    }

    private fun checkMicrophonePermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                initAudio()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestRecordAudio) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de micrófono concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Se necesita acceso al micrófono", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::recorder.isInitialized) recorder.stop()
        if (::player.isInitialized) player.stop()
    }
}