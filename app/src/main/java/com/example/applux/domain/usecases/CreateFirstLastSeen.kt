package com.example.applux.domain.usecases

import com.example.applux.OnlineOrOffline
import com.example.applux.Privacy
import com.example.applux.data.firebase.lastseen.LastSeenRepository
import com.example.applux.domain.models.LastSeen
import com.google.firebase.Timestamp
import javax.inject.Inject

class CreateFirstLastSeen @Inject constructor(
    private val lastSeenRepository: LastSeenRepository
) {

    operator fun invoke(){
        lastSeenRepository.createFirstLastSeen(LastSeen(
            Timestamp.now().seconds.toString(),
            OnlineOrOffline.OFFLINE,
            Privacy.PUBLIC)
        )
    }
}