package com.example.applux.ui.chat


data class ChatUiState(
    var chatItemUiState : HashMap<String?, ChatItemUiState> = HashMap()
)
