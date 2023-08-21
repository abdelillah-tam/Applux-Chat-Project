package com.example.applux.exceptions

import com.google.firebase.auth.AuthCredential

class EmailAlreadyInUseException : Exception()
data class EmailAlreadyExistsWithDifferentCredential(
    val email: String?,
    val newCredential: AuthCredential?
) : Exception()