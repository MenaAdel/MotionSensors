package com.t2.motionsensors.presentation

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.t2.motionsensors.R
import com.t2.motionsensors.databinding.FragmentVoiceBinding

class VoiceFragment : Fragment() {

    private val mediaRecorder = MediaRecorder()
    private lateinit var binding: FragmentVoiceBinding
    private val viewBinding get() = binding
    private val path: String by lazy { context?.cacheDir?.absolutePath + "myRec.3gp" }
    private var counter = 0

    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            binding.startBtn.isEnabled = true
        } else {
            Toast.makeText(activity, "permission denied", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVoiceBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        initView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
    }

    private fun requestPermission() {
        activity?.let {

            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE), 111)

        }
    }

    private fun initView() {
        with(binding) {
            playBtn.isEnabled = false
            stopBtn.isEnabled = false
            nextBtn.isEnabled = false
            startBtn.setOnClickListener {
                when(counter) {
                    0 -> showAlert(getString(R.string.alert_one))
                    1 -> showAlert(getString(R.string.alert_two))
                    2 -> showAlert(getString(R.string.alert_three))
                    3 -> showAlert(getString(R.string.alert_four))
                }
            }
            stopBtn.setOnClickListener {
                mediaRecorder.stop()
                startBtn.isEnabled = true
                playBtn.isEnabled = true
                stopBtn.isEnabled = false
                counter++
                updateCheckedValues()
            }
            playBtn.setOnClickListener {
                val mediaPlayer = MediaPlayer()
                try {
                    mediaPlayer.apply {
                        setDataSource(path)
                        prepare()
                        start()
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, "No voice recorded", Toast.LENGTH_LONG).show()
                }
            }

            nextBtn.setOnClickListener {
                NavHostFragment.findNavController(this@VoiceFragment)
                    .navigate(R.id.action_voiceFragment_to_cameraFragment)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.startBtn.isEnabled = true
            } else {
                shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
                requestPermission()
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

    private fun updateCheckedValues() {
        with(binding) {
            when (counter) {
                1 -> {
                    checkOne.setImageResource(R.drawable.ic_baseline_radio_button_checked_24)
                }
                2 -> {
                    checkTwo.setImageResource(R.drawable.ic_baseline_radio_button_checked_24)
                }
                3 -> {
                    checkThree.setImageResource(R.drawable.ic_baseline_radio_button_checked_24)
                }
                4 -> {
                    checkFour.setImageResource(R.drawable.ic_baseline_radio_button_checked_24)
                    nextBtn.isEnabled = true
                }
            }
        }
    }

    private fun showAlert(text: String) {
        AlertDialog.Builder(activity)
            .setTitle("You should say")
            .setMessage(text)
            .setOnDismissListener {
                startRecording()
            }
            .show()

    }
}