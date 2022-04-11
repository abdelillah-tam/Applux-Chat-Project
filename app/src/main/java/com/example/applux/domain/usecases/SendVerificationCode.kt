package com.example.applux.domain.usecases

import androidx.fragment.app.FragmentActivity
import com.example.applux.data.firebase.contactuser.ContactUserRepository
import com.google.firebase.auth.PhoneAuthProvider
import javax.inject.Inject

class SendVerificationCode @Inject constructor(
    private val contactUserRepository: ContactUserRepository
) {


    operator fun invoke(
        phone: String,
        require: FragmentActivity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        contactUserRepository.sendVerificationCode(
            phone,
            require,
            callback
        )

    }

}