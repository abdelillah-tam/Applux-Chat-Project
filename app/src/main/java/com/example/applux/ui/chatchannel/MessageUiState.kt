package com.example.applux.ui.chatchannel

import android.graphics.Bitmap
import com.example.applux.domain.models.Message

data class MessageUiState(
    var message: Message,
    var bitmap: Bitmap?
)