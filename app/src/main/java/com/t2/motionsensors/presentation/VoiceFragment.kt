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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.get
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

        setUpIndicator()
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
            continueBtn.isEnabled = false
            sayingText.text = context?.resources?.getText(R.string.alert_one)

            startBtn.setOnClickListener {
                startRecording()
            }


            continueBtn.setOnClickListener {
                counter++
                updateCheckedValues()
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
            binding.startBtn.setImageResource(R.drawable.ic_baseline_stop_24)
            binding.startBtn.isEnabled = true
            binding.startBtn.setOnClickListener {
                mediaRecorder.stop()
                binding.continueBtn.isEnabled = true


            }

        }
    }

    private fun updateCheckedValues() {
        with(binding) {
            when (counter) {
                1 -> {
                    setUpView(context?.resources?.getText(R.string.alert_two).toString())
                    setCurrentIndicator(0)
                }
                2 -> {
                    setUpView(context?.resources?.getText(R.string.alert_three).toString())
                    setCurrentIndicator(1)
                }
                3 -> {
                    setUpView(context?.resources?.getText(R.string.alert_four).toString())
                    setCurrentIndicator(2)
                }
                4 -> {
                    NavHostFragment.findNavController(this@VoiceFragment)
                        .navigate(R.id.action_voiceFragment_to_cameraFragment)
                }

            }
        }
    }

    private fun setUpView(text: String) {
        with(binding) {
            sayingText.text = text
            startBtn.setImageResource(R.drawable.ic_microphone_342)
            continueBtn.isEnabled = false
            startBtn.setOnClickListener {
                startRecording()
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


    private fun setUpIndicator() {
        val indicators = arrayOfNulls<ImageView>(4)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(context)
            indicators[i].apply {
                this?.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.indicator_inactive)
                )
                this?.layoutParams = layoutParams
            }
            viewBinding.indicatorsContainer.addView(indicators[i])

        }
    }


    private fun setCurrentIndicator(index: Int) {
        val imageView = viewBinding.indicatorsContainer[index] as ImageView
        imageView.setImageDrawable(
            context?.let { AppCompatResources.getDrawable(it, R.drawable.indicator_active) }
        )

    }

}