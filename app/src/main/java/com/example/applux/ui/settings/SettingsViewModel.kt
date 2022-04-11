package com.example.applux.ui.settings

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
    private val downloadProfilePicture: DownloadProfilePicture,
    private val firebase: FirebaseAuth,
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

                    if (!it.pic.equals("")) {
                        getProfilePictureFileViewModel(firebase.currentUser!!.uid, it.pic)
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

    private fun getProfilePictureFileViewModel(uid: String, fileName: String) {
        viewModelScope.launch {
            downloadProfilePicture(uid, fileName).collect {
                val byteArray = it
                _state.update {
                    it.copy(
                        profileBitmap = BitmapFactory.decodeByteArray(
                            byteArray,
                            0,
                            byteArray!!.size
                        )
                    )
                }
            }
        }
    }

    private fun updateProfilePictureFileNameViewModel(fileName: String){
        viewModelScope.launch {
            updateProfilePictureFileName(fileName).collect { result ->
                if (result){
                    getProfilePictureFileViewModel(firebase.currentUser!!.uid, fileName)
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
                                    message = "About is updated successfully"
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
                    val rawBit = Bitmap.createScaledBitmap(
                        event.bitmap,
                        ((event.bitmap.width * 0.3).toInt()),
                        ((event.bitmap.height * 0.3).toInt()),
                        false
                    )
                    val fileName = UUID.nameUUIDFromBytes(baos.toByteArray()).toString()
                    rawBit.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                    uploadProfilePicture(baos.toByteArray(), fileName).collect { result ->
                        _state.update { uiState ->
                            if (result) {
                                val messages = uiState.userMessages + UserMessage(
                                    id = UUID.randomUUID().mostSignificantBits,
                                    message = "Profile Picture is uploaded successfully"
                                )
                                updateProfilePictureFileNameViewModel(fileName)
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