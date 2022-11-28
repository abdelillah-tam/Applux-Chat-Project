package com.example.applux.data.firebase.profilepicture

import android.net.Uri
import android.util.Log
import com.example.applux.Privacy
import com.example.applux.domain.models.Picture
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "ProfilePictureRepositor"

class PictureRepositoryImpl @Inject constructor(
    val currentContactUserDocument: DocumentReference?,
    val contactCollectionReference: CollectionReference
) : PictureRepository {

    //val profilePictureBytesLiveDate = MutableLiveData<ByteArray>()

    override suspend fun getProfilePic(uid: String): Picture? {
        val profilePic = contactCollectionReference.document(uid)
            .collection("Profile")
            .document("profilepic")
            .get()
            .await().toObject(Picture::class.java)

        return profilePic
    }

    override suspend fun getProfilePic(): Picture? {
        val profilePic = currentContactUserDocument
            ?.collection("Profile")
            ?.document("profilepic")
            ?.get()
            ?.await()?.toObject(Picture::class.java)

        return profilePic
    }


    override suspend fun updateProfilePicturePrivacy(privacy: Privacy): Boolean {
        var result = false
        try {
            currentContactUserDocument?.collection("Profile")
                ?.document("profilepic")
                ?.update(mapOf("privacy" to privacy))
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        result = true
                    }
                }
                ?.addOnFailureListener {
                    result = false
                }?.await()
        } catch (exc: FirebaseFirestoreException) {
            if (exc.code == FirebaseFirestoreException.Code.NOT_FOUND) {
                result = false
            }
        }
        return result
    }

    override suspend fun updateProfilePictureFileName(fileName: String): Boolean {
        var result = false
        try {
            currentContactUserDocument?.collection("Profile")
                ?.document("profilepic")
                ?.update(mapOf("pic" to fileName))
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        result = true
                    }
                }
                ?.addOnFailureListener {
                    result = false
                }
                ?.await()
        } catch (exc: FirebaseFirestoreException) {
            if (exc.code == FirebaseFirestoreException.Code.NOT_FOUND) {
                currentContactUserDocument?.collection("Profile")
                    ?.document("profilepic")
                    ?.set(Picture(fileName, Privacy.PUBLIC))
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            result = true
                        }
                    }
                    ?.addOnFailureListener {
                        result = false
                    }
                    ?.await()
            }
        }
        return result
    }

    override suspend fun uploadProfilePicture(byte: ByteArray, fileName: String): Flow<Uri?> =
        flow {

            var downloadUri: Uri? = null
            withContext(Dispatchers.IO) {
                val ref = Firebase
                    .storage.reference
                    .child("Users")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child("profileImage")
                    .child(fileName)


                ref.putBytes(byte).continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    ref.downloadUrl
                }.addOnCompleteListener {
                    if (it.isSuccessful) {
                        downloadUri = it.result
                    }
                }.await()
            }

            emit(downloadUri)
        }


    override suspend fun uploadMessagePicture(
        receiverUid: String,
        byteArray: ByteArray,
        fileName: String
    ): Flow<Uri?> = flow {

        var downloadUri: Uri? = null
        withContext(Dispatchers.IO) {
            val ref = Firebase
                .storage.reference.child("Users").child(Firebase.auth.currentUser!!.uid)
                .child("chatChannels")
                .child(receiverUid).child(fileName)

            ref.putBytes(byteArray)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }

                    }
                    ref.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        downloadUri = task.result
                    }
                }.await()
        }
        emit(downloadUri)
    }

    override suspend fun downloadProfilePicture(
        uid: String,
        fileName: String
    ): ByteArray? {
        val byteArray = Firebase
            .storage
            .reference
            .child("Users")
            .child(uid)
            .child("profileImage")
            .child(fileName)
            .getBytes(1024L * 1024L).await()
        return byteArray
    }


}