package com.example.applux.data.firebase.message

import android.util.Log
import com.example.applux.domain.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "MessageRepositoryImpl"

class MessageRepositoryImpl @Inject constructor(
    private val contactCollectionReference: CollectionReference
) : MessageRepository {


    private val auth: FirebaseAuth = Firebase.auth

    override suspend fun sendMessage(
        message: Message,
        receiverUid: String
    ): Boolean {
        var isSent = false
        val createSender =
            contactCollectionReference.document(message.senderUID!!).collection("sharedChat")
                .document(receiverUid)
        val createReceiver =
            contactCollectionReference.document(receiverUid).collection("sharedChat")
                .document(message.senderUID!!)

        createSender.set(message)
        createReceiver.set(message)

        val sender = createSender.collection("messages")
        val receiver = createReceiver.collection("messages")

        sender.add(message).addOnCompleteListener {
            if (it.isSuccessful) {
                isSent = true
            }
        }
            .addOnFailureListener {
                Log.e(TAG, "sendMessage: ", it)
                isSent = false
                return@addOnFailureListener
            }.await()
        receiver.add(message).addOnCompleteListener {
            if (it.isSuccessful) {
                isSent = true
            }
        }
            .addOnFailureListener {
                Log.e(TAG, "sendMessage: ", it)
                isSent = false
                return@addOnFailureListener
            }.await()
        return isSent
    }


    override fun getUsersYouTalkedWith(): Flow<Set<Message>> = callbackFlow {
        var finalContactWithLastMessage = mutableSetOf<Message>()
        val streamingUsers = contactCollectionReference.document(auth.currentUser!!.uid)
            .collection("sharedChat").orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    value.documentChanges.forEach {
                        finalContactWithLastMessage+=it.document.toObject(Message::class.java)
                    }
                    try {
                        trySend(finalContactWithLastMessage)
                        finalContactWithLastMessage = mutableSetOf()
                    } catch (_: Throwable) {
                    }
                }
            }

        awaitClose { streamingUsers.remove() }
    }

    override fun listenForNewMessage(receiverUid: String) : Flow<List<Message>> = callbackFlow {
        val message = contactCollectionReference.document(auth.currentUser!!.uid)
            .collection("sharedChat").document(receiverUid)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null){
                    return@addSnapshotListener
                }
                if (value != null){
                    val messages = value.documentChanges.map {
                        it.document.toObject(Message::class.java)
                    }
                    try {
                        trySend(messages)
                    }catch (_: Throwable){

                    }
                }
            }

        awaitClose{ message.remove() }
    }



}