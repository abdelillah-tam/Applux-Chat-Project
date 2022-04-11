package com.example.applux.data.firebase.profilepicture

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.applux.Privacy
import com.example.applux.domain.models.Picture
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "ProfilePictureRepositor"

class PictureRepositoryImpl @Inject constructor(
    val currentContactUserDocument: DocumentReference,
    val contactCollectionReference: CollectionReference
) : PictureRepository {

    val profilePictureBytesLiveDate = MutableLiveData<ByteArray>()

    override suspend fun getProfilePic(uid: String): Picture? {
        var profilePic = contactCollectionReference.document(uid)
            .collection("Profile")
            .document("profilepic")
            .get()
            .await().toObject(Picture::class.java)

        return profilePic
    }

    override suspend fun getProfilePic(): Picture? {
        val profilePic = currentContactUserDocument
            .collection("Profile")
            .document("profilepic")
            .get()
            .await().toObject(Picture::class.java)

        return profilePic
    }


    override suspend fun updateProfilePicturePrivacy(privacy: Privacy): Boolean {
        var result = false
        currentContactUserDocument.collection("Profile")
            .document("profilepic")
            .update(mapOf("privacy" to privacy))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result = true
                }
            }
            .addOnFailureListener {
                result = false
            }.await()
        return result
    }

    override suspend fun updateProfilePictureFileName(fileName: String): Boolean {
        var result = false
        currentContactUserDocument.collection("Profile")
            .document("profilepic")
            .update(mapOf("pic" to fileName))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result = true
                }
            }
            .addOnFailureListener {
                result = false
            }
            .await()
        return result
    }

    override suspend fun uploadProfilePicture(byte: ByteArray, fileName: String): Boolean {
        var result = false
        Firebase
            .storage
            .reference
            .child("Users")
            .child(Firebase.auth.currentUser!!.uid)
            .child("profileImage")
            .child(fileName)
            .putBytes(byte)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result = true
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "uploadProfilePicture: ", it)
                result = false
            }
            .await()
        return result
    }

    /*override fun downloadProfilePicture(
        uid: String,
        fileName: String,
        lifecycleOwner: LifecycleOwner
    ) {
        Firebase
            .storage
            .reference
            .child("Users")
            .child(uid)
            .child("profileImage")
            .child(fileName)
            .getBytes(1024L * 1024L)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    profilePictureBytesLiveDate.value = it.result
                    if (profilePictureBytesLiveDate.hasActiveObservers()) {
                        profilePictureBytesLiveDate.removeObservers(lifecycleOwner)
                    }
                }
            }
    }
*/

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