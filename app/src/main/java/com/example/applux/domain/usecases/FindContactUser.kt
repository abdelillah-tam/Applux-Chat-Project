package com.example.applux.domain.usecases

import com.example.applux.data.firebase.contactuser.ContactUserRepository
import com.example.applux.domain.models.ContactUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FindContactUser @Inject constructor(
    private val contactUserRepository: ContactUserRepository
){

    operator fun invoke(contacts: HashMap<String, String>) : Flow<ArrayList<ContactUser>> = flow {
        contactUserRepository.findContactUser(contacts).collect {
            emit(it)
        }
    }

}