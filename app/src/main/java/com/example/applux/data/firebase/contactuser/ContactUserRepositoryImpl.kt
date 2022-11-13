package com.example.applux.data.firebase.contactuser

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.applux.domain.models.ContactUser
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "ContactUserRepositoryIm"

class ContactUserRepositoryImpl @Inject constructor(
    val currentContactUserDocument: DocumentReference?,
    val contactCollectionReference: CollectionReference
) : ContactUserRepository {


    private val auth = Firebase.auth

    override suspend fun createContactUser(contactUser: ContactUser): Boolean {
        var result = false
        contactCollectionReference.document(auth.currentUser!!.uid)
            .set(contactUser)
            .addOnCompleteListener {
                Log.e(TAG, "createContactUser: called" )
                if (it.isSuccessful) {
                    result = true
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "createContactUser: ", it)
                result = false
            }
            .await()
        return result
    }

    override suspend fun findContactUser(
        contacts: HashMap<String, String>
    ) : Flow<ArrayList<ContactUser>> = flow {
        val contactsList = ArrayList<ContactUser>()
        val contactsvalues: ArrayList<String> = ArrayList(contacts.values.toList())
        val chuncked = contactsvalues.chunked(10)

        chuncked.forEach {
            val p = contactCollectionReference.whereIn("phoneOrEmail", it).get().await()
            p.documents.forEach {
                val contact = it.toObject(ContactUser::class.java)
                if (contact != null && !contact.uid.equals(auth.currentUser!!.uid)) {
                    contactsList.add(contact)
                }
            }

        }
        emit(contactsList)
    }

    override fun getContactByUid(uid: String): Flow<ContactUser?> = flow {
        var contactUser: ContactUser? = null
        contactCollectionReference
            .document(uid)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    contactUser = it.result.toObject(ContactUser::class.java)!!
                }
            }
            .await()
        emit(contactUser)
    }

    override suspend fun checkIfContactAlreadyExist(phone: String) : Boolean {
        var result  = false
        contactCollectionReference.whereIn("phoneOrEmail", arrayListOf(phone))
            .get()
            .addOnSuccessListener {
                result = it.toObjects(ContactUser::class.java).isNotEmpty()
            }
            .addOnFailureListener {
                result = false
            }
            .await()
        return result
    }

    override fun getCurrentContact(): Flow<ContactUser?> = flow {
        val contactUser: ContactUser?
        contactUser = currentContactUserDocument
            ?.get()
            ?.await()?.toObject(ContactUser::class.java)
        emit(contactUser)

    }

    override suspend fun updateUsername(username: String): Boolean {
        var result = false
        try {
            currentContactUserDocument
                ?.update(mapOf("name" to username))
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        result = true
                    }
                }
                ?.addOnFailureListener {
                    Log.e(TAG, "updateUsername: ", it)
                    result = false
                }
                ?.await()
        }catch (exc : FirebaseFirestoreException){
            if (exc.code == FirebaseFirestoreException.Code.NOT_FOUND){

            }
        }
        return result
    }

    override fun sendVerificationCode(
        phone: String,
        require: FragmentActivity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(require)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    override suspend fun signInWithPhoneCredential(
        credential: PhoneAuthCredential
    ): Boolean {
        var result = false
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result = true
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "signInWithCredential: ", it)
                result = false
            }.await()


        return result
    }

    override suspend fun signInWithFacebookCredential(
        credential: AuthCredential
    ) : Boolean{
        var result = false
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    result = true
                }
            }
            .addOnFailureListener { result = false }
            .await()

        return result
    }

}