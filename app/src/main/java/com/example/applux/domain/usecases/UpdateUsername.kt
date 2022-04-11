package com.example.applux.domain.usecases

import com.example.applux.data.firebase.contactuser.ContactUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateUsername @Inject constructor(
    private val contactUserRepository: ContactUserRepository
) {

    operator fun invoke(username: String) : Flow<Boolean> = flow {
        val result = contactUserRepository.updateUsername(username)
        emit(result)
    }

}