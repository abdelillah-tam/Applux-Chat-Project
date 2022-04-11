package com.example.applux.domain.usecases

import com.example.applux.data.firebase.contactuser.ContactUserRepository
import com.example.applux.domain.models.ContactUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CheckIfContactAlreadyExist @Inject constructor(
    private val contactUserRepository: ContactUserRepository
) {

    operator fun invoke(phone: String) : Flow<Boolean> = flow {
        val result = contactUserRepository.checkIfContactAlreadyExist(phone)
        emit(result)
    }

}