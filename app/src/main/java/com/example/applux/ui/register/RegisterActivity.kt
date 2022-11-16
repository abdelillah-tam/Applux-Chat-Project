package com.example.applux.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.TwitterAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.Arrays

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navHost: NavHostFragment

    private lateinit var callbackManager: CallbackManager
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    private lateinit var gso: GoogleSignInOptions


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)
        navHost = supportFragmentManager.findFragmentById(R.id.registerfraghost) as NavHostFragment
        navController = navHost.navController


        binding.signInButton.setOnClickListener {
            signInWithGoogle()
        }

        binding.twitterLogin.setOnClickListener{
            signInWithTwitter()
        }

        callbackManager = CallbackManager.Factory.create()
        binding.loginButton.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("email", "public_profile"))
            LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        Log.e("TAG", "onCancel: called")
                    }

                    override fun onError(error: FacebookException) {
                        Log.e("TAG", "onError: ${error.printStackTrace()}")
                    }

                    override fun onSuccess(result: LoginResult) {
                        val credential =
                            FacebookAuthProvider.getCredential(result.accessToken.token)
                        registerViewModel.signInWithFacebookCredentialViewModel(credential)

                    }
                })

        }


    }



    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.registerfraghost) as NavHostFragment
        val navControllerUp = navHostFragment.navController
        return navControllerUp.navigateUp() || super.onSupportNavigateUp()
    }


    private fun signInWithGoogle() {
        gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestEmail()
            .requestIdToken(getString(R.string.server_client_id))
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    registerViewModel.signInWithGoogleCredentialViewModel(credential)
                } catch (e: ApiException) {
                    Log.e("TAG", "signInResult:failed code=" + e.statusCode)
                }

            }
        signInActivityResultLauncher.launch(mGoogleSignInClient.signInIntent)
    }

    private fun signInWithTwitter() {
        val provider = OAuthProvider.newBuilder("twitter.com")
        provider.addCustomParameter("lang","fr")
        val pending = Firebase.auth.pendingAuthResult
        if (pending != null){
            pending.addOnSuccessListener { authResult ->
                registerViewModel.signInWithTwitterCredentialViewModel(authResult.user!!.email!!)
            }
                .addOnFailureListener {

                }
        }else{
            Firebase.auth
                .startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener { authResult ->
                    registerViewModel.signInWithTwitterCredentialViewModel(authResult.additionalUserInfo!!.username!!)
                }
                .addOnFailureListener {

                }
        }
    }
}