package com.example.applux.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFullProfile(
    val getContact : GetContact,
    val getAbout: GetAbout,
    val getProfilePicture: GetProfilePicture,
    val getLastSeen: GetLastSeen
) {


}