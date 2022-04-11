package com.example.applux.domain.usecases

import com.example.applux.data.firebase.profilepicture.PictureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UploadProfilePicture @Inject constructor(
    private val pictureRepository: PictureRepository
) {

    operator fun invoke(byte: ByteArray,fileName: String) : Flow<Boolean> = flow {
        val result = pictureRepository.uploadProfilePicture(byte, fileName)
        emit(result)
    }

}