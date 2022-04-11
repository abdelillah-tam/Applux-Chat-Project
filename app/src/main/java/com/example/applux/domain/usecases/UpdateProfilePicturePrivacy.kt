package com.example.applux.domain.usecases

import com.example.applux.Privacy
import com.example.applux.data.firebase.profilepicture.PictureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateProfilePicturePrivacy @Inject constructor(
    private val pictureRepository: PictureRepository
) {

    operator fun invoke(privacy: Privacy) : Flow<Boolean> = flow {
        val result = pictureRepository.updateProfilePicturePrivacy(privacy)
        emit(result)
    }
}