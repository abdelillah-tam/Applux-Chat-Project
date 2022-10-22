package com.example.applux.ui.chat

import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.domain.models.Picture
import com.example.applux.domain.usecases.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getUsersYouTalkedWith: GetUsersYouTalkedWith,
    private val getContact: GetContact,
    private val getProfilePicture: GetProfilePicture,
    private val downloadProfilePicture: DownloadProfilePicture,
    private val userState: UserState,
    private val auth: FirebaseAuth
) : ViewModel() {


    private val _state = MutableLiveData<ChatUiState>()
    val state: LiveData<ChatUiState> = _state
    private val list = HashMap<String?, ChatItemUiState>()
    private var firstRun = true

    init {
        getUsersYouTalkedWithViewModel()
    }

    private fun getUsersYouTalkedWithViewModel() {
        viewModelScope.launch {
            val listOfMessages = getUsersYouTalkedWith()
            listOfMessages.collect {
                it.forEach { message ->
                    val messageStatementExpressionUid: String?
                    if (message.receiverUID == auth.currentUser!!.uid) {
                        messageStatementExpressionUid = message.senderUID
                    } else {
                        messageStatementExpressionUid = message.receiverUID
                    }

                    if (!messageStatementExpressionUid.equals("") && !list.containsKey(messageStatementExpressionUid)) {
                        val chatItemUiState = ChatItemUiState()
                        chatItemUiState.message = message
                        chatItemUiState.timestamp = message.timestamp

                        list.put(messageStatementExpressionUid, chatItemUiState)
                        getContactViewModel(
                            messageStatementExpressionUid!!
                        )
                        userStateViewModel(messageStatementExpressionUid)
                        getProfilePictureViewModel(
                            messageStatementExpressionUid,
                            chatItemUiState
                        )
                    }else{
                        val updatedChatItemUiState = list.get(messageStatementExpressionUid)
                            updatedChatItemUiState!!.message = message
                            updatedChatItemUiState.timestamp = message.timestamp
                        list.set(messageStatementExpressionUid, updatedChatItemUiState)
                    }

                }
                _state.value = ChatUiState(list)
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
                    if (_state.value != null) {
                        list.set(uid, newUpdate)
                        _state.value = ChatUiState(list)
                        downloadProfilePicture(uid, profilePicture, newUpdate)
                    }
                }

            }
        }
    }

    private fun getContactViewModel(uid: String) {
        viewModelScope.launch {
            getContact(uid).collect { contactUser ->
                if (_state.value != null) {
                    val chatItemUiState = list.get(uid)
                    if (chatItemUiState != null) {
                        chatItemUiState.contactUser = contactUser
                        list.set(uid, chatItemUiState)
                        _state.value = ChatUiState(list)
                    }
                }
            }
        }
    }

    private fun downloadProfilePicture(
        uid: String,
        profilePicture: Picture?,
        newUpdate: ChatItemUiState
    ) {
        viewModelScope.launch {
            downloadProfilePicture(
                uid,
                profilePicture!!.pic
            ).collect { byteArray ->
                if (byteArray != null) {
                    newUpdate.profileBitmap =
                        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                } else {
                    newUpdate.profileBitmap = null
                }
                if (_state.value != null) {
                    list.set(uid, newUpdate)
                    _state.value = ChatUiState(list)
                    firstRun = false
                }
            }
        }
    }

    fun userStateViewModel(uid: String){
        viewModelScope.launch {
            userState(uid).collect{
                if (it != null && list.containsKey(uid)){
                    val chatItemUiState = list.get(uid)
                    chatItemUiState!!.lastSeen = it
                    list.set(uid, chatItemUiState)
                    _state.value = ChatUiState(list)
                }
            }
        }
    }
}