package com.example.applux.data.firebase.lastseen

import android.util.Log
import com.example.applux.OnlineOrOffline
import com.example.applux.Privacy
import com.example.applux.domain.models.LastSeen
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
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

    override fun updateLastSeen(timestamp: String, onlineOrOffline: OnlineOrOffline) {
        try {
            currentContactUserDocument?.collection("Profile")
                ?.document("lastseen")
                ?.update(mapOf("timestamp" to timestamp, "onlineOrOffline" to onlineOrOffline))
        }catch (exc : FirebaseFirestoreException){
            if (exc.code == FirebaseFirestoreException.Code.NOT_FOUND){
                currentContactUserDocument?.collection("Profile")
                    ?.document("lastseen")
                    ?.set(LastSeen(timestamp, onlineOrOffline, Privacy.PUBLIC))
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

}