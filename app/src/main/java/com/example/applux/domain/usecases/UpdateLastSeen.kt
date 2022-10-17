package com.example.applux.domain.usecases

import com.example.applux.OnlineOrOffline
import com.example.applux.data.firebase.lastseen.LastSeenRepository
import javax.inject.Inject

class UpdateLastSeen @Inject constructor(
    private val lastSeenRepository : LastSeenRepository
) {

    operator fun invoke(timestamp: String, onlineOrOffline: OnlineOrOffline){
        lastSeenRepository.updateLastSeen(timestamp, onlineOrOffline)
    }

}