package com.example.applux.domain.usecases

import com.example.applux.Privacy
import com.example.applux.data.firebase.about.AboutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateAboutPrivacy @Inject constructor(
    private val aboutRepository: AboutRepository
) {

    operator fun invoke(privacy: Privacy) : Flow<Boolean> = flow {
        val result = aboutRepository.updateAboutPrivacy(privacy)
        emit(result)
    }

}