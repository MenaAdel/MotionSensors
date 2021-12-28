package com.t2.motionsensors.presentation.info

import SensorReport
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.t2.motionsensors.databinding.ActivityInfoBinding
import com.t2.motionsensors.presentation.voice.VoiceMainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding
    private var sensorReport: SensorReport? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sensorReport = SensorReport(this, this)

        initView()
    }

    private fun initView() {
        binding.continueBtn.setOnClickListener {
            startActivity(Intent(this , VoiceMainActivity::class.java))
        }
    }

    override fun onStop() {
        super.onStop()
        sensorReport?.stopWorkers()
        sensorReport = null
    }
}