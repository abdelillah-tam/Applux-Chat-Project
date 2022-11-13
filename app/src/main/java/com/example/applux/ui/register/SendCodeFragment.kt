package com.example.applux.ui.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.example.applux.R
import com.example.applux.databinding.FragmentSendCodeBinding
import com.example.applux.ui.main.MainActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SendCodeFragment : Fragment(R.layout.fragment_send_code) {


    private lateinit var verificationId: String
    private lateinit var phone: String

    private val registerViewModel: RegisterViewModel by activityViewModels()

    private lateinit var binding: FragmentSendCodeBinding



    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSendCodeBinding.bind(view)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.state.collect {
                    if (it.isExist){
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    if (it.isSignedIn){
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
            }
        }
        binding.registerButton.setOnClickListener {
            phone = binding.registerPhoneEdittext.editText?.text.toString()
            registerViewModel.sendVerificationCodeViewModel(
                phone,
                requireActivity(),
                callbacks
            )
        }




    }



    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            registerViewModel.signInWithPhoneCredentialViewModel(p0, phone)
        }

        override fun onVerificationFailed(p0: FirebaseException) {

        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            verificationId = p0
            registerViewModel.savePhone(phone)
            registerViewModel.saveVerificationId(verificationId)
            findNavController().navigate(R.id.action_sendCodeFragment_to_enterCodeFragment)
        }
    }
}