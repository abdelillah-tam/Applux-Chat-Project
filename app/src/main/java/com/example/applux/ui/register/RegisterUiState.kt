package com.example.applux.ui.register


data class RegisterUiState(
    val phone: String? = null,
    val verificationId: String? = null,
    val isSignedIn: Boolean = false,
    val isExist: Boolean = false
)
