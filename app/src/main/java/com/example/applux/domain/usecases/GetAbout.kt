package com.example.applux.domain.usecases

import com.example.applux.data.firebase.about.AboutRepository
import com.example.applux.domain.models.About
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAbout @Inject constructor(
    private val aboutRepository: AboutRepository
){
    operator fun invoke() : Flow<About?> = flow {
        val about = aboutRepository.getAbout()
        emit(about)
    }

    operator fun invoke(uid: String) : Flow<About?> = flow {
        val about = aboutRepository.getAbout(uid)
        emit(about)
    }
}