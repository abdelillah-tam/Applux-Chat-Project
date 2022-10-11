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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "MessageRepositoryImpl"

class MessageRepositoryImpl @Inject constructor(
   val contactCollectionReference: CollectionReference

) : MessageRepository {


    private val auth: FirebaseAuth = Firebase.auth

    override suspend fun sendMessage(
        message: Message
    ): Boolean {
        var isSent = false
        val createSender =
            contactCollectionReference.document(message.senderUID!!).collection("sharedChat")
                .document(message.receiverUID!!)
        val createReceiver =
            contactCollectionReference.document(message.receiverUID!!).collection("sharedChat")
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

    override suspend fun getAllMessages(
        receiverUid: String,
        firstTime: Boolean,
        position: Message?
    ): Flow<QuerySnapshot?> = flow {

        val messages = contactCollectionReference.document(auth.currentUser!!.uid)
            .collection("sharedChat").document(receiverUid).collection("messages")

        var gotMsgs: QuerySnapshot? = null
        if (firstTime) {
            gotMsgs =
                messages.limit(30).orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().await()

        } else {
            gotMsgs =
                messages.limit(30).orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(position?.timestamp)
                    .get().await()
        }

        if (gotMsgs != null && gotMsgs.size() != 0) {
            emit(gotMsgs)
        }
    }
    override fun getUsersYouTalkedWith(): Flow<Set<Message>> = callbackFlow {
        var finalContactWithLastMessage = emptySet<Message>()
        var finalContactWithLastMessage4 = emptySet<Message>()
        val streamingUsers = contactCollectionReference.document(auth.currentUser!!.uid)
            .collection("sharedChat").orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    finalContactWithLastMessage+=value.toObjects(Message::class.java)
                    /*value.documentChanges.forEach {
                        Log.e(TAG, "getUsersYouTalkedWith: " + it.document.toObject(Message::class.java).text )
                    }*/
                    try {
                        trySend(finalContactWithLastMessage)
                        finalContactWithLastMessage = emptySet()
                    } catch (e: Throwable) {

                    }
                }
            }

        awaitClose { streamingUsers.remove() }
    }

    override fun listenForNewMessage(receiverUid: String) : Flow<Message?> = callbackFlow {
        val message = contactCollectionReference.document(auth.currentUser!!.uid)
            .collection("sharedChat").document(receiverUid)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null){
                    return@addSnapshotListener
                }
                if (value != null){
                    val message = value.documents[0].toObject(Message::class.java)
                    try {
                        trySend(message)
                    }catch (e: Throwable){

                    }
                }
            }

        awaitClose{ message.remove() }
    }


}