package com.example.applux.ui.contacts

import android.graphics.Bitmap
import com.example.applux.domain.models.ContactUser
import com.example.applux.domain.models.Picture

data class ContactsItemUiState(
    var picture: Picture? = null,
    var contactUser: ContactUser? = null,
    var profileBitmap: Bitmap? = null
)