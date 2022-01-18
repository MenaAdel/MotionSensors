package com.t2.motionsensors.presentation

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.t2.motionsensors.R
import com.t2.motionsensors.databinding.FragmentSplashBinding
import com.t2.motionsensors.domain.entity.Users

class SplashFragment : Fragment() {

    private var binding: FragmentSplashBinding? = null
    private val viewBinding get() = binding!!
    private val accounts: Array<String> by lazy { arrayOf("T2") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding?.apply {
            accountText.setOnClickListener {
                showAlert()
            }
            continueBtn.apply {
                setOnClickListener {
                    initSensorData(userId.text.toString().trim(), Users.accountId)
                }
            }
        }
    }

    private fun showAlert(){
        AlertDialog.Builder(activity)
            .setSingleChoiceItems(accounts ,0) { dialog, position ->
                Users.accountId = accounts[position]
                binding?.accountText?.text = accounts[position]
                dialog.dismiss()
            }
            .show()
    }

    private fun initSensorData(userId: String, accountId: String?) {
        Users.userId = userId
        Users.isFirstTime = true
        if (userId.isNotEmpty()) {
            Users.accountId = if (accountId.isNullOrEmpty()) null else accountId
            NavHostFragment.findNavController(this@SplashFragment)
                .navigate(R.id.action_splashFragment_to_infoFragment)
        } else {
            Toast.makeText(activity, "Please, enter user id", Toast.LENGTH_LONG).show()
        }
    }
}