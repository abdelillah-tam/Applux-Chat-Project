package com.example.applux.domain.usecases

import com.example.applux.data.firebase.lastseen.LastSeenRepository
import com.example.applux.domain.models.LastSeen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserState @Inject constructor(
    private val lastSeenRepository: LastSeenRepository
) {

    operator suspend fun invoke(uid: String) : Flow<LastSeen?> = flow {
        lastSeenRepository.state(uid).collect{
            emit(it)
        }
    }
}