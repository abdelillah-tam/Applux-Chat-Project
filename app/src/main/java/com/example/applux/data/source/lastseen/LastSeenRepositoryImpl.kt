package com.example.applux.data.firebase.lastseen

import android.util.Log
import com.example.applux.OnlineOrOffline
import com.example.applux.Privacy
import com.example.applux.domain.models.LastSeen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "LastSeenRepositoryImpl"

class LastSeenRepositoryImpl @Inject constructor(
    private val currentContactUserDocument: DocumentReference?,
    private val contactCollectionReference: CollectionReference

) : LastSeenRepository {


    override suspend fun getLastSeen(): LastSeen? {
        val lastSeen = currentContactUserDocument?.collection("Profile")
            ?.document("lastseen")
            ?.get()
            ?.await()?.toObject(LastSeen::class.java)
        return lastSeen
    }

    override suspend fun getLastSeen(uid: String): LastSeen? {
        val lastSeen = contactCollectionReference.document(uid)
            .collection("Profile")
            .document("lastseen")
            .get().await().toObject(LastSeen::class.java)
        return lastSeen
    }

    override suspend fun updateLastSeen(timestamp: String, onlineOrOffline: OnlineOrOffline) {
        try {
            currentContactUserDocument?.collection("Profile")
                ?.document("lastseen")
                ?.update(mapOf("timestamp" to timestamp, "onlineOrOffline" to onlineOrOffline))
                ?.await()
        }catch (exc : FirebaseFirestoreException){
            if (exc.code == FirebaseFirestoreException.Code.NOT_FOUND){
                currentContactUserDocument?.collection("Profile")
                    ?.document("lastseen")
                    ?.set(LastSeen(Firebase.auth.currentUser!!.uid,timestamp, onlineOrOffline, Privacy.PUBLIC))
                    ?.await()
            }
        }
    }

    override suspend fun updateLastSeenPrivacy(privacy: Privacy): Boolean {
        var result = false
        currentContactUserDocument?.collection("Profile")
            ?.document("lastseen")
            ?.update(mapOf("privacy" to privacy))
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    result = true
                }
            }
            ?.addOnFailureListener {
                Log.e(TAG, "updateLastSeenPrivacy: ", it)
                result = false
            }
            ?.await()

        return result
    }

    override suspend fun state(uid: String) : Flow<LastSeen?> = callbackFlow{
        val lastseenListening = contactCollectionReference.document(uid).collection("Profile")
            .document("lastseen")
            .addSnapshotListener { value, error ->
                if (error != null){
                    return@addSnapshotListener
                }

                if (value != null){
                    val lastSeen = value.toObject(LastSeen::class.java)
                    trySend(lastSeen)
                }
            }

        awaitClose{lastseenListening.remove()}
    }
}