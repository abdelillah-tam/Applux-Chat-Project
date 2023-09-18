package com.example.applux.data.firebase.profilepicture

import android.net.Uri
import com.example.applux.Privacy
import com.example.applux.domain.models.Picture
import kotlinx.coroutines.flow.Flow

interface PictureRepository {

    suspend fun getProfilePic(uid: String) : Picture?

    suspend fun getProfilePic() : Picture?

    suspend fun updateProfilePicturePrivacy(privacy: Privacy) : Boolean

    suspend fun updateProfilePictureFileName(fileName: String) : Boolean

    suspend fun uploadProfilePicture(byte: ByteArray, fileName: String) : Flow<Uri?>

    suspend fun downloadProfilePicture(uid: String, fileName: String) : ByteArray?

    suspend fun uploadMessagePicture(receiverUid: String, byteArray: ByteArray, fileName: String) : Flow<Uri?>

}