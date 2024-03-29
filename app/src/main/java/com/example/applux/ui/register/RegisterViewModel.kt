package com.example.applux.ui.register

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.domain.models.ContactUser
import com.example.applux.domain.usecases.*
import com.google.firebase.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val sendVerificationCode: SendVerificationCode,
    private val signInWithPhoneCredential: SignInWithPhoneCredential,
    private val signInWithFacebookCredential: SignInWithFacebookCredential,
    private val signInWithGoogleCredential: SignInWithGoogleCredential,
    private val checkIfContactAlreadyExist: CheckIfContactAlreadyExist,
    private val createContactUser: CreateContactUser
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()


    fun sendVerificationCodeViewModel(
        phone: String,
        require: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        viewModelScope.launch {
            sendVerificationCode(
                phone,
                require,
                callback
            )
        }
    }

    fun signInWithPhoneCredentialViewModel(p0: PhoneAuthCredential, phone: String) {
        viewModelScope.launch {
            signInWithPhoneCredential(p0).collect { result ->
                if (result) {
                    checkIfContactAlreadyExist(phone).collect { exist ->
                        if (exist) {
                            _state.update {
                                it.copy(isExist = exist)
                            }
                        } else {
                            createCompleteAccountViewModel(phone)
                        }
                    }

                }
            }
        }
    }

    private fun createCompleteAccountViewModel(phoneOrEmail: String) {
        viewModelScope.launch {
            val contactUser = ContactUser(
                auth.currentUser!!.uid,
                phoneOrEmail,
                ""
            )
            createContactUser(contactUser).collect {
                if (it) {
                    _state.update {
                        it.copy(isSignedIn = true)
                    }
                }
            }
        }
    }


    fun signInWithGoogleCredentialViewModel(p0: AuthCredential) {
        viewModelScope.launch {
            signInWithGoogleCredential(p0).collect { result ->
                if (result) {
                    checkIfContactAlreadyExist(auth.currentUser!!.email!!).collect { exist ->
                        if (exist) {
                            _state.update {
                                it.copy(isExist = exist)
                            }
                        } else {
                            createCompleteAccountViewModel(auth.currentUser!!.email!!)
                        }
                    }
                }

            }
        }
    }

}