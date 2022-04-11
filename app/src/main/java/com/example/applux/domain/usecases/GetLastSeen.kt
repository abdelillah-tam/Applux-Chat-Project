package com.example.applux.domain.usecases

import com.example.applux.data.firebase.lastseen.LastSeenRepository
import com.example.applux.domain.models.LastSeen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetLastSeen @Inject constructor(
    private val lastSeenRepository: LastSeenRepository
) {


    operator fun invoke() : Flow<LastSeen?> = flow{
        val lastSeen = lastSeenRepository.getLastSeen()
        emit(lastSeen)
    }

    operator fun invoke(uid: String) : Flow<LastSeen?> = flow{
        val lastSeen = lastSeenRepository.getLastSeen(uid)
        emit(lastSeen)
    }
}