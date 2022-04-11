package com.example.applux.domain.usecases

import javax.inject.Inject

data class UpdatePrivacies @Inject constructor(
    val updateAboutPrivacy: UpdateAboutPrivacy,
    val updateProfilePicturePrivacy: UpdateProfilePicturePrivacy,
    val updateLastSeenPrivacy: UpdateLastSeenPrivacy
)