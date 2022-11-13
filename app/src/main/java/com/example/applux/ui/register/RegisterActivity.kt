package com.example.applux.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.applux.R
import com.example.applux.databinding.ActivityRegisterBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginClient
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.Arrays

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navHost: NavHostFragment


    private lateinit var callbackManager: CallbackManager
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)
        navHost = supportFragmentManager.findFragmentById(R.id.registerfraghost) as NavHostFragment
        navController = navHost.navController


        callbackManager = CallbackManager.Factory.create()
        binding.loginButton.setReadPermissions("email", "public_profile")
        binding.loginButton
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Log.e("TAG", "onCancel: called")
                }

                override fun onError(error: FacebookException) {
                    Log.e("TAG", "onError: ${error.printStackTrace()}")
                }

                override fun onSuccess(result: LoginResult) {
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    registerViewModel.signInWithFacebookCredentialViewModel(credential)

                }
            })



    }


    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.registerfraghost) as NavHostFragment
        val navControllerUp = navHostFragment.navController
        return navControllerUp.navigateUp() || super.onSupportNavigateUp()
    }


}