package com.t2.motionsensors.presentation

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.t2.motionsensors.R
import com.t2.motionsensors.databinding.FragmentCameraBinding
import com.t2.motionsensors.domain.entity.Users

class CameraFragment : Fragment() {

    private var binding: FragmentCameraBinding? = null
    private val viewBinding get() = binding!!
    private var listener: EndSessionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? EndSessionListener?
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View{
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            if (ActivityCompat.checkSelfPermission(it,
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(it,
                    arrayOf(android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 200)
            }
            initView()
        }
    }

    private fun initView() {
        binding?.takePhoto?.setOnClickListener {
            capturePhoto()
        }
        binding?.signUp?.setOnClickListener {
            Users.apply {
                userId = ""
                accountId = null
            }
            listener?.onEndSession()
            NavHostFragment.findNavController(this@CameraFragment)
                .navigate(R.id.action_cameraFragment_to_splashFragment)
        }
    }

    private fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 200 && data != null){
            binding?.imageView2?.setImageBitmap(data.extras?.get("data") as Bitmap)
            binding?.signUp?.visibility = View.VISIBLE
        }
    }
}