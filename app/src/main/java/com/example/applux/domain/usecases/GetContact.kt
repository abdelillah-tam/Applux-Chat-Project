package com.example.applux.domain.usecases

import com.example.applux.data.firebase.contactuser.ContactUserRepository
import com.example.applux.domain.models.ContactUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetContact @Inject constructor(
    private val contactUserRepository: ContactUserRepository
) {

    operator fun invoke(uid: String) : Flow<ContactUser?> = flow {
        contactUserRepository.getContactByUid(uid).collect {
            emit(it)
        }

    }

    operator fun invoke() : Flow<ContactUser?> = flow {
        contactUserRepository.getCurrentContact().collect {
            emit(it)
        }

    }
}