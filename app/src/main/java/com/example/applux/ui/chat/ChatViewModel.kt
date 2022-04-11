package com.example.applux.ui.chat

import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.domain.models.Picture
import com.example.applux.domain.usecases.DownloadProfilePicture
import com.example.applux.domain.usecases.GetContact
import com.example.applux.domain.usecases.GetProfilePicture
import com.example.applux.domain.usecases.GetUsersYouTalkedWith
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getUsersYouTalkedWith: GetUsersYouTalkedWith,
    private val getContact: GetContact,
    private val getProfilePicture: GetProfilePicture,
    private val downloadProfilePicture: DownloadProfilePicture,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    init {
        getUsersYouTalkedWithViewModel()
    }

    private fun getUsersYouTalkedWithViewModel() {
        viewModelScope.launch {
            val listOfMessages = getUsersYouTalkedWith()
            listOfMessages.collect {
                val list = ArrayList<ChatItemUiState>()
                it.forEach {
                    val chatItemUiState = ChatItemUiState()
                    chatItemUiState.message = it
                    var position = -1
                    val messageStatementExpressionUid =
                        if (it.receiverUID == auth.currentUser!!.uid) it.senderUID else it.receiverUID

                    if (!messageStatementExpressionUid.equals("")) {
                        list.also {
                            it.add(chatItemUiState)
                            position = it.indexOf(chatItemUiState)
                        }
                        getContactViewModel(messageStatementExpressionUid!!, position, chatItemUiState)
                        getProfilePictureViewModel(messageStatementExpressionUid!!, position, chatItemUiState)
                    }
                    
                }
                _state.update { chatuistate ->
                    chatuistate.copy(chatItemUiState = list)
                }
            }
        }
    }

    private fun getProfilePictureViewModel(
        uid: String,
        position: Int,
        newUpdate: ChatItemUiState
    ) {
        viewModelScope.launch {
            getProfilePicture(uid).collect { profilePicture ->
                if (profilePicture != null) {
                    newUpdate.picture = profilePicture
                    _state.update {
                        val newList = it.copy().chatItemUiState
                        newList.set(position, newUpdate)
                        it.copy(
                            chatItemUiState = newList,
                            newUpdate = newUpdate
                        )
                    }
                    downloadProfilePicture(uid, profilePicture, position, newUpdate)
                }

            }
        }
    }

    private fun getContactViewModel(uid: String, position: Int, newUpdate: ChatItemUiState) {
        viewModelScope.launch {
            getContact(uid).collect { contactUser ->
                newUpdate.contactUser = contactUser
                _state.update {
                    val newList = it.copy().chatItemUiState
                    newList.set(position, newUpdate)
                    it.copy(chatItemUiState = newList, newUpdate = newUpdate)
                }
            }
        }
    }

    private fun downloadProfilePicture(
        uid: String,
        profilePicture: Picture?,
        position: Int,
        newUpdate: ChatItemUiState
    ) {
        viewModelScope.launch {
            downloadProfilePicture(
                uid,
                profilePicture!!.pic
            ).collect { byteArray ->
                if (byteArray != null) {
                    newUpdate.profileBitmap =
                        BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                } else {
                    newUpdate.profileBitmap = null
                }
                _state.update {
                    val newList = it.copy().chatItemUiState
                    newList.set(position, newUpdate)
                    it.copy(chatItemUiState = newList, newUpdate = newUpdate)
                }
            }
        }
    }

}