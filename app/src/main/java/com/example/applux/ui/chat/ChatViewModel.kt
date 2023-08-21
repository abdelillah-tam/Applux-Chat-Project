package com.example.applux.ui.chat

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.domain.usecases.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getUsersYouTalkedWith: GetUsersYouTalkedWith,
    private val getContact: GetContact,
    private val getProfilePicture: GetProfilePicture,
    private val userState: UserState
) : ViewModel() {


    private val _state = mutableStateMapOf<String, ChatItemUiState>()
    val state = _state

    private var firstRun = true

    init {
        getUsersYouTalkedWithViewModel()
    }

    private fun getUsersYouTalkedWithViewModel() {
        viewModelScope.launch {
            val listOfMessages = getUsersYouTalkedWith()
            listOfMessages.collect {
                it.forEach { message ->

                    val messageStatementExpressionUid: String? =
                        if (Firebase.auth.currentUser!!.uid != message.senderUID) message.senderUID else message.receiverUid


                    val chatItemUiState: ChatItemUiState
                    if (!messageStatementExpressionUid.equals("")
                    ) {
                        chatItemUiState = ChatItemUiState()
                        chatItemUiState.message = message
                        chatItemUiState.timestamp = message.timestamp

                        getContactViewModel(
                            messageStatementExpressionUid!!
                        )
                        userStateViewModel(messageStatementExpressionUid)
                        getProfilePictureViewModel(
                            messageStatementExpressionUid,
                            chatItemUiState
                        )
                    } else {
                        chatItemUiState = _state[messageStatementExpressionUid]!!
                        chatItemUiState.message = message
                        chatItemUiState.timestamp = message.timestamp
                    }
                    _state[messageStatementExpressionUid!!] = chatItemUiState

                }

            }
        }
    }

    private fun getProfilePictureViewModel(
        uid: String,
        newUpdate: ChatItemUiState
    ) {
        viewModelScope.launch {
            getProfilePicture(uid).collect { profilePicture ->
                if (profilePicture != null) {
                    newUpdate.picture = profilePicture
                    _state.remove(uid)
                    _state[uid] = newUpdate
                }

            }
        }
    }

    private fun getContactViewModel(uid: String) {
        viewModelScope.launch {
            getContact(uid).collect { contactUser ->
                val chatItemUiState = _state.get(uid)
                if (chatItemUiState != null) {
                    chatItemUiState.contactUser = contactUser
                    _state.remove(uid)
                    _state[uid] = chatItemUiState

                }
            }
        }
    }

    fun userStateViewModel(uid: String) {
        viewModelScope.launch {
            userState(uid).collect {
                if (it != null) {
                    val chatItemUiState = _state[uid]
                    chatItemUiState!!.lastSeen = it
                    _state.remove(uid)
                    _state[uid] = chatItemUiState
                }
            }
        }
    }
}

