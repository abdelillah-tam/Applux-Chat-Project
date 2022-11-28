package com.example.applux.ui.chatchannel

import android.graphics.Bitmap
import com.example.applux.domain.models.About
import com.example.applux.domain.models.ContactUser
import com.example.applux.domain.models.LastSeen
import com.example.applux.domain.models.Message
import com.google.firebase.firestore.DocumentSnapshot

data class ChatChannelUiState(
    val profilePictureBitmap: Bitmap? = null,
    val contactUser: ContactUser? = null,
    val messages : HashMap<String, MessageUiState> = HashMap(),
    val about: About? = null,
    val lastSeen: LastSeen? = null,
    val isSent: Boolean = false,
    val firstTime: Boolean = true,
    val position: Message? = null,
    val newMessage: Message? = null,
    var disableLoading: Boolean = false
)