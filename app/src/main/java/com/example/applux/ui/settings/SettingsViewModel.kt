package com.example.applux.ui.settings

import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.domain.models.UserMessage
import com.example.applux.domain.usecases.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getContact: GetContact,
    private val getAbout: GetAbout,
    private val getProfilePicture: GetProfilePicture,
    private val getLastSeen: GetLastSeen,
    private val updateAbout: UpdateAbout,
    private val updateUsername: UpdateUsername,
    private val updatePrivacies: UpdatePrivacies,
    private val uploadProfilePicture: UploadProfilePicture,
    private val updateProfilePictureFileName: UpdateProfilePictureFileName
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        getContactViewModel()
        getAboutViewModel()
        getLastSeenViewModel()
        getProfilePictureViewModel()
    }

    private fun getContactViewModel() {
        getContact().onEach {
            val contactUser = it
            _state.update {
                it.copy(contactUser = contactUser)
            }
        }.launchIn(viewModelScope)

    }

    private fun getAboutViewModel() {
        viewModelScope.launch {
            getAbout().collect {
                val about = it
                _state.update {
                    it.copy(about = about)
                }
            }
        }
    }

    private fun getProfilePictureViewModel() {
        viewModelScope.launch {
            getProfilePicture().collect {
                if (it != null) {
                    val profilePicture = it
                    _state.update {
                        it.copy(picture = profilePicture)
                    }
                }
            }
        }
    }

    private fun getLastSeenViewModel() {
        viewModelScope.launch {
            getLastSeen().collect { lastSeen ->
                _state.update {
                    it.copy(lastSeen = lastSeen)
                }
            }
        }
    }



    private fun updateProfilePictureFileNameViewModel(fileLink: String){
        viewModelScope.launch {
            updateProfilePictureFileName(fileLink).collect { result ->
                if (result){

                }
            }
        }

    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdateAbout -> {
                viewModelScope.launch {
                    updateAbout(event.about).collect { result ->
                        _state.update { uiState ->
                            if (result) {
                                val messages = uiState.userMessages + UserMessage(
                                    id = UUID.randomUUID().mostSignificantBits,
                                    message = "About is updated successfully"
                                )
                                uiState.copy(userMessages = messages)
                            } else {
                                val messages = uiState.userMessages + UserMessage(
                                    id = UUID.randomUUID().mostSignificantBits,
                                    message = "About is failed to update"
                                )
                                uiState.copy(userMessages = messages)
                            }
                        }
                    }
                }
            }
            is SettingsEvent.UpdateUsername -> {
                viewModelScope.launch {
                    updateUsername(event.username).collect { result ->
                        _state.update { uiState ->
                            if (result) {
                                val messages = uiState.userMessages + UserMessage(
                                    id = UUID.randomUUID().mostSignificantBits,
                                    message = "Username is updated successfully"
                                )
                                uiState.copy(userMessages = messages)
                            } else {
                                val messages = uiState.userMessages + UserMessage(
                                    id = UUID.randomUUID().mostSignificantBits,
                                    message = "Username is failed to update"
                                )
                                uiState.copy(userMessages = messages)
                            }

                        }
                    }

                }
            }
            is SettingsEvent.UpdateAboutPrivacy -> {
                viewModelScope.launch {
                    updatePrivacies.updateAboutPrivacy(event.privacy).collect { result ->
                        _state.update { uiState ->
                            if (result) {
                                val messages = uiState.userMessages + UserMessage(
                                    id = UUID.randomUUID().mostSignificantBits,
                                    message = "Privacy's About is updated successfully"
                                )
                                uiState.copy(userMessages = messages)
                            } else {
                                val messages = uiState.userMessages + UserMessage(
                                    id = UUID.randomUUID().mostSignificantBits,
                                    message = "Privacy's About is failed to update"
                                )
                                uiState.copy(userMessages = messages)
                            }

                        }
                    }
                }
            }
            is SettingsEvent.UpdateProfilePicturePrivacy -> {
                viewModelScope.launch {
                    updatePrivacies.updateProfilePicturePrivacy(event.privacy).collect { result ->
                        _state.update { uiState ->
                            if (result) {
                                val messages = uiState.userMessages + UserMessage(
                                    id = UUID.randomUUID().mostSignificantBits,
                                    message = "Privacy's Profile Picture is updated successfully"
                                )

                                uiState.copy(userMessages = messages)
                            } else {
                                val messages = uiState.userMessages + UserMessage(
                                    id = UUID.randomUUID().mostSignificantBits,
                                    message = "Privacy's Profile Picture is failed to update"
                                )
                                uiState.copy(userMessages = messages)
                            }

                        }
                    }
                }
            }
            is SettingsEvent.UpdateLastSeenPrivacy -> {
                viewModelScope.launch {
                    updatePrivacies.updateLastSeenPrivacy(event.privacy).collect { result ->
                        _state.update { uiState ->
                            if (result) {
                                val messages = uiState.userMessages + UserMessage(
                                    id = UUID.randomUUID().mostSignificantBits,
                                    message = "Privacy's Last Seen is updated successfully"
                                )
                                uiState.copy(userMessages = messages)
                            } else {
                                val messages = uiState.userMessages + UserMessage(
                                    id = UUID.randomUUID().mostSignificantBits,
                                    message = "Privacy's Last Seen is failed to update"
                                )
                                uiState.copy(userMessages = messages)
                            }
                        }

                    }
                }
            }
            is SettingsEvent.UploadProfilePicture -> {
                viewModelScope.launch {
                    val baos = ByteArrayOutputStream()

                    val bitmap = MediaStore.Images.Media.getBitmap(event.contentResolver, event.uri)

                    if (bitmap.byteCount > 500000) {
                        bitmap.compress(Bitmap.CompressFormat.WEBP, 40, baos)
                    }else bitmap.compress(Bitmap.CompressFormat.WEBP, 100, baos)

                    val fileName = UUID.nameUUIDFromBytes(baos.toByteArray()).toString()

                    uploadProfilePicture(baos.toByteArray(), fileName).collect { result ->
                        _state.update { uiState ->
                            if (result != null) {
                                val messages = uiState.userMessages + UserMessage(
                                    id = UUID.randomUUID().mostSignificantBits,
                                    message = "Profile Picture is uploaded successfully"
                                )
                                updateProfilePictureFileNameViewModel(result.toString())
                                uiState.copy(userMessages = messages)
                            } else {
                                val messages = uiState.userMessages + UserMessage(
                                    id = UUID.randomUUID().mostSignificantBits,
                                    message = "Profile Picture is failed to upload"
                                )
                                uiState.copy(userMessages = messages)
                            }
                        }
                    }
                }
            }
        }
    }

    fun userMessageShown(id: Long) {
        _state.update { uiState ->
            val messages = uiState.userMessages.filterNot {
                it.id == id
            }
            uiState.copy(userMessages = messages)
        }
    }

}