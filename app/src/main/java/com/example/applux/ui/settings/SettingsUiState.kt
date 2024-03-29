package com.example.applux.ui.settings

import com.example.applux.domain.models.*

data class SettingsUiState(
    var contactUser: ContactUser? = null,
    val about: About? = null,
    val picture: Picture? = null,
    val lastSeen: LastSeen? = null,
    val userMessages: List<UserMessage> = emptyList()
)
