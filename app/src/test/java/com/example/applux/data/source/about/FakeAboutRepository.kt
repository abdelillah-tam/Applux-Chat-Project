package com.example.applux.data.firebase.about

import com.example.applux.Privacy
import com.example.applux.domain.models.About

class FakeAboutRepository : AboutRepository {

    private val about = About("This is About for testing", Privacy.CONTACTS)


    override suspend fun updateAbout(about: String?): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateAboutPrivacy(privacy: Privacy): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAbout(uid: String): About? {
        return about
    }

    override suspend fun getAbout(): About? {
        return about
    }

    override fun createAbout() {
        TODO("Not yet implemented")
    }


}