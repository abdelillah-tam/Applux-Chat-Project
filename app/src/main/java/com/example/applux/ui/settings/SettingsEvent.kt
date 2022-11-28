package com.example.applux.ui.settings

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import com.example.applux.Privacy

sealed class SettingsEvent{
    data class UpdateUsername(val username: String) : SettingsEvent()
    data class UpdateAbout(val about: String) : SettingsEvent()
    data class UploadProfilePicture(val uri: Uri, val contentResolver: ContentResolver) : SettingsEvent()
    data class UpdateProfilePicturePrivacy(val privacy: Privacy) : SettingsEvent()
    data class UpdateLastSeenPrivacy(val privacy: Privacy) : SettingsEvent()
    data class UpdateAboutPrivacy(val privacy: Privacy) : SettingsEvent()
}
