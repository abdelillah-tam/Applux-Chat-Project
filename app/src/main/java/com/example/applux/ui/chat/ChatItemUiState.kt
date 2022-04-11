package com.example.applux.ui.chat

import android.graphics.Bitmap
import android.util.Log
import com.example.applux.domain.models.ContactUser
import com.example.applux.domain.models.Message
import com.example.applux.domain.models.Picture

data class ChatItemUiState(
    var picture: Picture? = null,
    var message: Message? = null,
    var contactUser: ContactUser? = null,
    var profileBitmap: Bitmap? = null
)