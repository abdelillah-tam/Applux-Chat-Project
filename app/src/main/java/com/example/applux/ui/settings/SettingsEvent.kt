package com.example.applux.ui.settings

import android.graphics.Bitmap
import com.example.applux.Privacy

sealed class SettingsEvent{
    data class UpdateUsername(val username: String) : SettingsEvent()
    data class UpdateAbout(val about: String) : SettingsEvent()
    data class UploadProfilePicture(val bitmap: Bitmap) : SettingsEvent()
    data class UpdateProfilePicturePrivacy(val privacy: Privacy) : SettingsEvent()
    data class UpdateLastSeenPrivacy(val privacy: Privacy) : SettingsEvent()
    data class UpdateAboutPrivacy(val privacy: Privacy) : SettingsEvent()
}
