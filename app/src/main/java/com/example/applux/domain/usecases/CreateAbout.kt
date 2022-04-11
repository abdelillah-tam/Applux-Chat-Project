package com.example.applux.domain.usecases

import com.example.applux.data.firebase.about.AboutRepository
import javax.inject.Inject

class CreateAbout @Inject constructor(
    private val aboutRepository: AboutRepository
) {

    operator fun invoke(){
        aboutRepository.createAbout()
    }

}