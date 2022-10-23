package com.example.applux.ui.profile

import android.graphics.Bitmap
import com.example.applux.domain.models.About
import com.example.applux.domain.models.ContactUser

data class ProfileUiState(
    val contactUser: ContactUser? = null,
    val about: About? = null,
    val profileBitmap: Bitmap? = null
)