package com.example.applux.data.firebase.lastseen

import com.example.applux.OnlineOrOffline
import com.example.applux.Privacy
import com.example.applux.domain.models.LastSeen
import kotlinx.coroutines.flow.Flow

interface LastSeenRepository {


    suspend fun getLastSeen() : LastSeen?

    suspend fun getLastSeen(uid: String) : LastSeen?

    suspend fun updateLastSeen(timestamp: String, onlineOrOffline: OnlineOrOffline)

    suspend fun updateLastSeenPrivacy(privacy: Privacy) : Boolean

    suspend fun state(uid: String) : Flow<LastSeen?>

}