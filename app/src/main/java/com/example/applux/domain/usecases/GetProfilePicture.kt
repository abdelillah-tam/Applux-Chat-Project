package com.example.applux.domain.usecases

import com.example.applux.data.firebase.profilepicture.PictureRepository
import com.example.applux.domain.models.Picture
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProfilePicture @Inject constructor(
    private val pictureRepository : PictureRepository
) {

    operator fun invoke() : Flow<Picture?> = flow {
        val profilePicture = pictureRepository.getProfilePic()
        emit(profilePicture)
    }

    operator fun invoke(uid: String) : Flow<Picture?> = flow {
        val profilePicture = pictureRepository.getProfilePic(uid)
        emit(profilePicture)
    }
}