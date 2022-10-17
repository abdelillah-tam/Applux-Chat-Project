package com.example.applux.data.firebase.lastseen

import com.example.applux.OnlineOrOffline
import com.example.applux.Privacy
import com.example.applux.domain.models.LastSeen

interface LastSeenRepository {


    suspend fun getLastSeen() : LastSeen?

    suspend fun getLastSeen(uid: String) : LastSeen?

    fun updateLastSeen(timestamp: String, onlineOrOffline: OnlineOrOffline)

    suspend fun updateLastSeenPrivacy(privacy: Privacy) : Boolean



}