package com.example.applux.domain.usecases


import com.example.applux.data.firebase.about.AboutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateAbout @Inject constructor(
    private val aboutRepository: AboutRepository
){
    operator fun invoke(about: String?) : Flow<Boolean> = flow {
        val finalResult = aboutRepository.updateAbout(about)
        emit(finalResult)
    }
}