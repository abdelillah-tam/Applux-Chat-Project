package com.example.applux.domain.usecases

import android.net.Uri
import com.example.applux.data.firebase.profilepicture.PictureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UploadProfilePicture @Inject constructor(
    private val pictureRepository: PictureRepository
) {

    operator fun invoke(byte: ByteArray,fileName: String) : Flow<Uri?> = flow {
        pictureRepository.uploadProfilePicture(byte, fileName).collect{
            if (it != null){
                emit(it)
            }
        }

    }

}