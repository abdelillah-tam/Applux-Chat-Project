package com.example.applux.domain.usecases

import com.example.applux.data.firebase.contactuser.ContactUserRepository
import com.example.applux.domain.models.ContactUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateContactUser @Inject constructor(
    private val contactUserRepository: ContactUserRepository
) {

    operator fun invoke(contactUser: ContactUser) : Flow<Boolean> = flow{
        val result = contactUserRepository.createContactUser(contactUser)
        emit(result)
    }

}