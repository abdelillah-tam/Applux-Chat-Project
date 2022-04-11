package com.example.applux.data.firebase.profilepicture

import com.example.applux.Privacy
import com.example.applux.domain.models.Picture

interface PictureRepository {

    suspend fun getProfilePic(uid: String) : Picture?

    suspend fun getProfilePic() : Picture?

    suspend fun updateProfilePicturePrivacy(privacy: Privacy) : Boolean

    suspend fun updateProfilePictureFileName(fileName: String) : Boolean

    suspend fun uploadProfilePicture(byte: ByteArray, fileName: String) : Boolean

    //fun downloadProfilePicture(uid: String,fileName: String, lifecycleOwner: LifecycleOwner)

    suspend fun downloadProfilePicture(uid: String, fileName: String) : ByteArray?


}