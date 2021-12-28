package com.t2.motionsensors.presentation.voice

import SensorReport
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.t2.motionsensors.R
import com.t2.motionsensors.databinding.ActivityVoiceMainBinding

@RequiresApi(Build.VERSION_CODES.N)
class VoiceMainActivity : AppCompatActivity() {

    private var mediaRecorder: MediaRecorder? = null
    private lateinit var binding: ActivityVoiceMainBinding
    var path = ""

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //SensorReport(this ,this)
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
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            binding.startBtn.isEnabled = true
            mediaRecorder = MediaRecorder()
            path = cacheDir.absolutePath + "myRec.3gp"
        } else {
            binding.startBtn.isEnabled = false
        }

    }

    private fun initView() {
        with(binding) {
            startBtn.isEnabled = false
            stopBtn.isEnabled = false
            startBtn.setOnClickListener {
                startRecording()
            }
            stopBtn.setOnClickListener {
                mediaRecorder?.stop()
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
        }
    }

    private fun startRecording() {
        mediaRecorder?.apply {
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
}