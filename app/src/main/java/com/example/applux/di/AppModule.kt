package com.example.applux.di

import androidx.fragment.app.FragmentActivity
import com.example.applux.ui.MainActivity
import com.example.applux.ui.MainFragment
import com.example.applux.data.firebase.about.AboutRepository
import com.example.applux.data.firebase.about.AboutRepositoryImpl
import com.example.applux.data.firebase.contactuser.ContactUserRepository
import com.example.applux.data.firebase.contactuser.ContactUserRepositoryImpl
import com.example.applux.data.firebase.lastseen.LastSeenRepository
import com.example.applux.data.firebase.lastseen.LastSeenRepositoryImpl
import com.example.applux.data.firebase.message.MessageRepository
import com.example.applux.data.firebase.message.MessageRepositoryImpl
import com.example.applux.data.firebase.profilepicture.PictureRepository
import com.example.applux.data.firebase.profilepicture.PictureRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {




    @Singleton
    @Provides
    fun provideAuth(): FirebaseAuth {
        return Firebase.auth
    }



    @Provides
    fun provideFireStoreInstance(): FirebaseFirestore {
        val db = FirebaseFirestore.getInstance()
        return db
    }


    @Provides
    fun provideMainActivity(): MainActivity {
        return MainActivity()
    }


    @Provides
    fun provideMainFragment(): MainFragment {
        return MainFragment()
    }


    @Provides
    fun provideFragmentActivity(): FragmentActivity {
        return FragmentActivity()
    }

    @Singleton
    @Provides
    fun currentContactUserDocument(firestore: FirebaseFirestore): DocumentReference {
        return firestore.collection("ContactUser").document(Firebase.auth.currentUser!!.uid)
    }


    @Provides
    fun contactCollectionReference(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection("ContactUser")
    }



    @Singleton
    @Provides
    fun contactUserRepository(
        currentContactUserDocument: DocumentReference,
        contactCollectionReference: CollectionReference
    ): ContactUserRepository {
        return ContactUserRepositoryImpl(currentContactUserDocument, contactCollectionReference)
    }

    @Singleton
    @Provides
    fun lastSeenRepository(
        currentContactUserDocument: DocumentReference,
        contactCollectionReference: CollectionReference
    ): LastSeenRepository {
        return LastSeenRepositoryImpl(currentContactUserDocument, contactCollectionReference)
    }

    @Singleton
    @Provides
    fun aboutRepository(
        currentContactUserDocument: DocumentReference,
        contactCollectionReference: CollectionReference
    ) : AboutRepository{
        return AboutRepositoryImpl(currentContactUserDocument, contactCollectionReference)
    }

    @Singleton
    @Provides
    fun profilePictureRepository(
        currentContactUserDocument: DocumentReference,
        contactCollectionReference: CollectionReference
    ) : PictureRepository{
        return PictureRepositoryImpl(currentContactUserDocument, contactCollectionReference)
    }

    @Singleton
    @Provides
    fun messageRepository(
        contactCollectionReference: CollectionReference
    ) : MessageRepository{
        return MessageRepositoryImpl(contactCollectionReference)
    }

}