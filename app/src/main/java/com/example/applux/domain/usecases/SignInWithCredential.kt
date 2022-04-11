package com.example.applux.domain.usecases

import com.example.applux.data.firebase.contactuser.ContactUserRepository
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignInWithCredential @Inject constructor(
    private val contactUserRepository: ContactUserRepository
) {

    operator fun invoke(p0: PhoneAuthCredential) : Flow<Boolean> = flow{
        val result = contactUserRepository.signInWithCredential(p0)
        emit(result)
    }
}