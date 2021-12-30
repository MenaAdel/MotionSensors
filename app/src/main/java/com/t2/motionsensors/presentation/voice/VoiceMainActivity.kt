package com.t2.motionsensors.presentation.voice

import SensorReport
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.t2.motionsensors.databinding.ActivityVoiceMainBinding
import com.t2.motionsensors.presentation.camera.CameraActivity

@RequiresApi(Build.VERSION_CODES.N)
class VoiceMainActivity : AppCompatActivity() {

    private val mediaRecorder = MediaRecorder()
    private lateinit var binding: ActivityVoiceMainBinding
    val path: String  by lazy {cacheDir.absolutePath + "myRec.3gp" }
    var sensorReport: SensorReport? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sensorReport = SensorReport(this ,this)
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE ,android.Manifest.permission.MANAGE_EXTERNAL_STORAGE), 111)
        }

        initView()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        binding.startBtn.isEnabled = requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED

    }

    private fun initView() {
        with(binding) {
            startBtn.setOnClickListener {
                startRecording()
            }
            stopBtn.setOnClickListener {
                mediaRecorder.stop()
                startBtn.isEnabled = true
                stopBtn.isEnabled = false
            }
            playBtn.setOnClickListener {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.apply {
                    setDataSource(path)
                    prepare()
                    start()
                }
            }

            nextBtn.setOnClickListener {
                startActivity(Intent(this@VoiceMainActivity , CameraActivity::class.java))
            }
        }
    }

    private fun startRecording() {
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(path)
            prepare()
            start()
            binding.stopBtn.isEnabled = true
            binding.startBtn.isEnabled = false
        }
    }

    override fun onStop() {
        super.onStop()
        sensorReport?.stopWorkers()
        sensorReport = null
    }
}