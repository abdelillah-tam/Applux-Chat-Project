package com.example.applux.domain.usecases

import android.net.Uri
import com.example.applux.data.firebase.profilepicture.PictureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UploadMessagePicture @Inject constructor(
    private val pictureRepository: PictureRepository
) {
    operator fun invoke(receiverUid: String, byteArray: ByteArray, fileName: String) : Flow<Uri?> = flow{
        pictureRepository.uploadMessagePicture(receiverUid, byteArray, fileName).collect{
            emit(it)
        }

    }
}