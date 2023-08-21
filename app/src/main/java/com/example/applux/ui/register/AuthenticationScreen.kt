package com.example.applux.ui.register

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.applux.ui.theme.Green_600
import com.example.applux.ui.theme.Orange_500
import com.example.applux.widgets.CustomTextField
import com.example.applux.R
import com.example.applux.findActivity
import com.example.applux.widgets.Hint
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

@Composable
fun AuthenticationScreen(
    modifier: Modifier = Modifier,
    dependingHeight: Double,
    registerViewModel: RegisterViewModel = hiltViewModel(),
    onExist: () -> Unit,
    systemUiController: SystemUiController
) {

    var phone by remember {
        mutableStateOf("")
    }

    val registerState by registerViewModel.state.collectAsState()

    val context = LocalContext.current

    var token by remember {
        mutableStateOf("")
    }

    var verificationId by remember {
        mutableStateOf("")
    }

    var code by remember{
        mutableStateOf("")
    }

    systemUiController.setSystemBarsColor(Color.White)

    lateinit var signInActivityResultLauncher: ActivityResultLauncher<Intent>

    LaunchedEffect(registerState.isExist, registerState.isSignedIn) {
        if (registerState.isExist || registerState.isSignedIn) {
            onExist()
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
        }
    }

    signInActivityResultLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                registerViewModel.signInWithGoogleCredentialViewModel(credential)
            } catch (e: ApiException) {
                Log.e("TAG", "signInResult:failed code=" + e.statusCode)
            }

        }


    Scaffold(modifier = modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                modifier = modifier.padding((30 * dependingHeight).dp),
                text = "Applux",
                fontSize = (88 * dependingHeight).sp
            )

            Text(
                modifier = modifier,
                text = "Register a new account",
                fontSize = (16 * dependingHeight).sp
            )
            Box(modifier.height((64 * dependingHeight).dp))

            if (verificationId.isEmpty()) {
                CustomTextField(
                    modifier = modifier
                        .height((64 * dependingHeight).dp)
                        .padding(horizontal = (24 * dependingHeight).dp),
                    value = phone,
                    onValueChange = { newValue ->
                        phone = newValue
                    },
                    borderColor = Color.Gray,
                    focusedBorder = Orange_500,
                    cornerSize = (16 * dependingHeight).dp,
                    hint = {
                        Hint(
                            text = "Your phone number …",
                            fontSize = (18 * dependingHeight).sp
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = (18 * dependingHeight).sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    )
                )
            } else {
                CustomTextField(
                    modifier = modifier
                        .height((64 * dependingHeight).dp)
                        .padding(horizontal = (24 * dependingHeight).dp),
                    value = code,
                    onValueChange = { newValue ->
                        code = newValue
                    },
                    borderColor = Color.Gray,
                    focusedBorder = Orange_500,
                    cornerSize = (16 * dependingHeight).dp,
                    hint = {
                        Hint(
                            text = "Verification Code",
                            fontSize = (18 * dependingHeight).sp
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = (18 * dependingHeight).sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
            }

            Button(
                modifier = modifier,
                onClick = {
                    if(verificationId.isEmpty()) {
                        registerViewModel.sendVerificationCodeViewModel(
                            phone,
                            context.findActivity(),
                            callbacks
                        )
                    }else if(code.isNotEmpty()) {
                        val credential = PhoneAuthProvider.getCredential(verificationId, code)
                        registerViewModel.signInWithPhoneCredentialViewModel(credential, phone)
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Green_600),
                shape = RoundedCornerShape((16 * dependingHeight).dp)
            ) {
                Text("Register", color = Color.White, fontSize = (18.sp) * dependingHeight)
            }
            Box(modifier.height((64 * dependingHeight).dp))

            Button(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = (48 * dependingHeight).dp)
                    .height((64 * dependingHeight).dp),
                onClick = {
                    signInWithGoogle(signInActivityResultLauncher, context)
                },
                shape = RoundedCornerShape((16 * dependingHeight).dp),
                border = BorderStroke(1.dp, Orange_500),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
            ) {
                Image(
                    modifier = modifier
                        .width((28 * dependingHeight).dp)
                        .height((28 * dependingHeight).dp),
                    painter = painterResource(id = R.drawable.icons8_google),
                    contentDescription = ""
                )
                Text(text = "Sign in with Google", fontSize = (18.sp) * dependingHeight)
            }
            Box(modifier.height((48 * dependingHeight).dp))
        }
    }


}

fun signInWithGoogle(
    signInActivityResultLauncher: ActivityResultLauncher<Intent>,
    context: Context
) {
    val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(
        GoogleSignInOptions.DEFAULT_SIGN_IN
    ).requestEmail()
        .requestIdToken(context.getString(R.string.server_client_id))
        .build()

    val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)

    signInActivityResultLauncher.launch(mGoogleSignInClient.signInIntent)
}

