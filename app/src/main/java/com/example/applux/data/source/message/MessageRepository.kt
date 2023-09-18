package com.example.applux.data.firebase.message

import com.example.applux.domain.models.Message
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun sendMessage(message: Message, receiverUid: String) : Boolean

    fun getUsersYouTalkedWith() : Flow<Set<Message>>

    fun listenForNewMessage(receiverUid: String) : Flow<List<Message>>
}