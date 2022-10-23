package com.example.applux.ui.profile

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.domain.models.ContactUser
import com.example.applux.domain.usecases.GetAbout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val about: GetAbout
) : ViewModel() {

    private val _state  = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    fun getAboutViewModel(uid: String){
        viewModelScope.launch {
            about(uid).collect{ about ->
                _state.update {
                    it.copy(about = about)
                }
            }
        }
    }

    fun setContactUserViewModel(contactUser: ContactUser){
        viewModelScope.launch {
            _state.update {
                it.copy(contactUser = contactUser)
            }
        }
    }

    fun setProfileBitmapViewModel(bitmap: Bitmap?){
        viewModelScope.launch {
            _state.update {
                it.copy(profileBitmap = bitmap)
            }
        }
    }
}