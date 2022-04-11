package com.example.applux.ui.register

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.domain.models.ContactUser
import com.example.applux.domain.usecases.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val sendVerificationCode: SendVerificationCode,
    private val signInWithCredential: SignInWithCredential,
    private val checkIfContactAlreadyExist: CheckIfContactAlreadyExist,
    private val createContactUser: CreateContactUser,
    private val createFirstLastSeen: CreateFirstLastSeen,
    private val createAbout: CreateAbout
) : ViewModel(){

    private val _state = MutableStateFlow(RegisterUiState())
    val state : StateFlow<RegisterUiState> = _state.asStateFlow()


    fun sendVerificationCodeViewModel(
        phone: String,
        require: FragmentActivity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ){
        viewModelScope.launch {
            sendVerificationCode(
                phone,
                require,
                callback
            )
        }
    }

    fun signInWithCredentialViewModel(p0: PhoneAuthCredential, phone: String){
        viewModelScope.launch {
            signInWithCredential(p0).collect { result ->
                if (result){
                    checkIfContactAlreadyExist(phone).collect { result ->
                        if (result){
                            _state.update {
                                it.copy(isExist = result)
                            }
                        }else{
                            _state.update {
                                it.copy(isExist = result)
                            }
                        }
                    }

                }
            }
        }
    }

    fun createCompleteAccountViewModel(phone: String){
        viewModelScope.launch {
            val contactUser = ContactUser(auth.currentUser!!.uid,
                phone,
                ""
                )
            createContactUser(contactUser).collect {
                if (it){
                    _state.update {
                        it.copy(isSignedIn = true)
                    }
                    createFirstLastSeen()
                    createAbout()
                }
            }
        }
    }


    fun savePhone(phone: String){
        _state.update {
            it.copy(phone = phone)
        }
    }

    fun saveVerificationId(verificationId: String){
        _state.update {
            it.copy(verificationId = verificationId)
        }
    }
}