package com.example.applux.data.firebase.about

import android.util.Log
import com.example.applux.Privacy
import com.example.applux.domain.models.About
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "AboutRepositoryImpl"
class AboutRepositoryImpl @Inject constructor(
    val currentContactUserDocument : DocumentReference,
    val contactCollectionReference : CollectionReference
) : AboutRepository {

    override suspend fun updateAbout(about: String?) : Boolean {
        var result = false
        currentContactUserDocument.collection("Profile")
            .document("about")
            .update(mapOf("about" to about))
            .addOnCompleteListener {
                if (it.isSuccessful){
                    result = true
                }
            }
            .addOnFailureListener {
                result = false
            }
            .await()
        return result
    }

    override suspend fun updateAboutPrivacy(privacy: Privacy) : Boolean {
        var result = false
        currentContactUserDocument.collection("Profile")
            .document("about")
            .update(mapOf("privacy" to privacy))
            .addOnCompleteListener {
                if (it.isSuccessful){
                    result = true
                }
            }
            .addOnFailureListener {
                result = false
            }
            .await()

        return result
    }

    override suspend fun getAbout(uid: String) : About?{
        val about = contactCollectionReference.document(uid)
            .collection("Profile")
            .document("about")
            .get()
            .await().toObject(About::class.java)
        return about
    }

    override suspend fun getAbout() : About?{
            val about = currentContactUserDocument
                .collection("Profile")
                .document("about")
                .get()
                .await().toObject(About::class.java)
            return about
    }

    override fun createAbout() {
        currentContactUserDocument
            .collection("Profile")
            .document("about")
            .set(About("", Privacy.PUBLIC))
            .addOnFailureListener {
                Log.e(TAG, "createAbout: ", it)
            }

    }
}