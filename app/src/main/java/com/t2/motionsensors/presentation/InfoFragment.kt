package com.t2.motionsensors.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.t2.motionsensors.R
import com.t2.motionsensors.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    private var binding: FragmentInfoBinding? = null
    private val viewBinding get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding?.continueBtn?.setOnClickListener {
            validateInfo()
        }
    }

    private fun validateInfo() {
        binding?.apply {
            when {
                passwordEdt.text.toString() != confirmPasswordEdt.text.toString() ->
                    getString(R.string.password_confirm).showToast()
                firstEdt.text.toString().isNotEmpty() && secondEdt.text.toString().isNotEmpty() &&
                        jobNumberEdt.text.toString().isNotEmpty() &&
                        emailEdt.text.toString().isNotEmpty() &&
                        passwordEdt.text.toString().isNotEmpty() &&
                        confirmPasswordEdt.text.toString().isNotEmpty() ->
                    NavHostFragment.findNavController(this@InfoFragment)
                        .navigate(R.id.action_infoFragment_to_voiceFragment)
                else -> {
                    getString(R.string.fill_data).showToast()

                }

            }
        }
    }

    private fun String.showToast() {
        Toast.makeText(requireContext(), this, Toast.LENGTH_LONG).show()
    }
}