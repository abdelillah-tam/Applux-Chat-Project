package com.example.applux.domain.usecases

import com.example.applux.data.firebase.profilepicture.PictureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateProfilePictureFileName @Inject constructor(
    private val pictureRepository: PictureRepository
)
{

    operator fun invoke(fileName: String) : Flow<Boolean> = flow {
        val result = pictureRepository.updateProfilePictureFileName(fileName)
        emit(result)
    }


}