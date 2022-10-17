package com.example.applux.ui.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.applux.R
import com.example.applux.databinding.FragmentEnterCodeBinding
import com.example.applux.ui.main.MainActivity
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EnterCodeFragment : Fragment(R.layout.fragment_enter_code) {


    private lateinit var verificationId: String
    private lateinit var phone: String

    private val registerViewModel: RegisterViewModel by activityViewModels()
    private lateinit var binding: FragmentEnterCodeBinding


    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEnterCodeBinding.bind(view)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.state.collect {
                    if (it.verificationId != null) {
                        verificationId = it.verificationId
                    }
                    if (it.phone != null) {
                        phone = it.phone
                    }
                    if (it.isExist) {
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    if (it.isSignedIn) {
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }

            }
        }

        binding.buttonVerify.setOnClickListener {
            val code = binding.codeVerify.editText?.text.toString()
            verificationCode(code)
        }

    }


    private fun verificationCode(code: String) {
        Log.e("TAG", "verificationCode: " + code)
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        registerViewModel.signInWithCredentialViewModel(credential, phone)
    }


}

