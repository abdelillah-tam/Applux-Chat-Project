package com.example.applux.domain.usecases

import com.example.applux.data.firebase.contactuser.ContactUserRepository
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignInWithFacebookCredential @Inject constructor(
    private val contactUserRepository: ContactUserRepository
) {

    operator fun invoke(p0: AuthCredential) : Flow<Boolean> = flow{
        val result = contactUserRepository.signInWithFacebookCredential(p0)
        emit(result)
    }

}