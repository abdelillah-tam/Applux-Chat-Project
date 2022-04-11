package com.example.applux.ui.chat


data class ChatUiState(
    var chatItemUiState : ArrayList<ChatItemUiState> = ArrayList(),
    var newUpdate: ChatItemUiState? = null
)
