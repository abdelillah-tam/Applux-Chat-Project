package com.example.applux.domain.usecases

import com.example.applux.data.firebase.profilepicture.PictureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DownloadProfilePicture @Inject constructor(
    private val pictureRepository: PictureRepository
) {

    operator fun invoke(uid: String, fileName: String) : Flow<ByteArray?> = flow {
        if (!fileName.equals("")) {
            val profilePictureFile = pictureRepository.downloadProfilePicture(uid, fileName)
            emit(profilePictureFile)
        }
        else{
            emit(null)
        }
    }
}