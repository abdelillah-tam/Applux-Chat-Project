package com.example.applux

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


const val FIREBASE_USER_DATASTORE_NAME = "firebaseuserdatastorename"
val FIREBASE_USER_USERNAME = stringPreferencesKey("username")
val FIREBASE_USER_USERNAME_PRIVACY = stringPreferencesKey("usernamePrivacy")
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = FIREBASE_USER_DATASTORE_NAME)


object FirebaseUserDataStore {

    suspend fun writeUsername(context: Context, username: String){
        context.dataStore.edit { usernameData ->
            usernameData[FIREBASE_USER_USERNAME] = username
        }
    }

    suspend fun getUsername(context: Context) : Flow<String?>{
        return context.dataStore.data.map { usernameData ->
            usernameData[FIREBASE_USER_USERNAME]
        }
    }
}