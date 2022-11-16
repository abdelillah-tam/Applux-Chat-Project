package com.example.applux.data.firebase.contactuser

import androidx.fragment.app.FragmentActivity
import com.example.applux.domain.models.ContactUser
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow

interface ContactUserRepository {

    suspend fun createContactUser(contactUser: ContactUser): Boolean

    suspend fun findContactUser(contacts: HashMap<String, String>) : Flow<ArrayList<ContactUser>>

    fun getContactByUid(uid: String): Flow<ContactUser?>

    suspend fun checkIfContactAlreadyExist(phone: String): Boolean

    fun getCurrentContact(): Flow<ContactUser?>

    suspend fun updateUsername(username: String): Boolean

    fun sendVerificationCode(phone: String, require: FragmentActivity, callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks)

    suspend fun signInWithPhoneCredential(
        credential: PhoneAuthCredential
    ) : Boolean

    suspend fun signInWithFacebookCredential(
        credential: AuthCredential
    ) : Boolean
    suspend fun signInWithGoogleCredential(
        credential: AuthCredential
    ) : Boolean
    suspend fun signInWithTwitterCredential(
        credential: AuthCredential
    ) : Boolean
}