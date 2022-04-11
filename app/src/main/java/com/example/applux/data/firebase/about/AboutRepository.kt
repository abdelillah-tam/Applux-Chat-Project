package com.example.applux.data.firebase.about

import com.example.applux.Privacy
import com.example.applux.domain.models.About


interface AboutRepository {

    suspend fun updateAbout(about: String?) : Boolean

    suspend fun updateAboutPrivacy(privacy: Privacy) : Boolean

    suspend fun getAbout(uid: String) : About?

    suspend fun getAbout() : About?

    fun createAbout()
}