package com.example.applux.data.firebase.message

import com.example.applux.domain.models.Message
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun sendMessage(message: Message) : Boolean

    suspend fun getAllMessages(receiverUid: String, firstTime: Boolean, position: Message?) : Flow<QuerySnapshot?>

    fun getUsersYouTalkedWith() : Flow<Set<Message>>

    fun listenForNewMessage(receiverUid: String) : Flow<Message?>
}