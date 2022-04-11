package com.example.applux.domain.usecases

import com.example.applux.Privacy
import com.example.applux.data.firebase.lastseen.LastSeenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateLastSeenPrivacy @Inject constructor(
    private val lastSeenRepository: LastSeenRepository
){

    operator fun invoke(privacy: Privacy) : Flow<Boolean> = flow {
        val result = lastSeenRepository.updateLastSeenPrivacy(privacy)
        emit(result)
    }

}