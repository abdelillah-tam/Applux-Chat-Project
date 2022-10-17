package com.example.applux.ui.chatchannel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.domain.models.ContactUser
import com.example.applux.domain.models.Message
import com.example.applux.domain.usecases.GetAbout
import com.example.applux.domain.usecases.GetAllMessages
import com.example.applux.domain.usecases.ListenForNewMessage
import com.example.applux.domain.usecases.SendMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatChannelViewModel @Inject constructor(
    private val getAbout: GetAbout,
    private val sendMessage: SendMessage,
    private val getAllMessages: GetAllMessages,
    private val listenForNewMessage: ListenForNewMessage
) : ViewModel() {


    private val _state = MutableStateFlow(ChatChannelUiState())
    val state = _state.asStateFlow()



    fun getProfilePictureAndContactFromFragmentViewModel(
        pictureBitmap: Bitmap?,
        contactUser: ContactUser
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    profilePictureBitmap = pictureBitmap,
                    contactUser = contactUser
                )
            }
            getAboutViewModel(contactUser.uid!!)
        }
    }

    private fun getAboutViewModel(uid: String) {
        viewModelScope.launch {
            getAbout(uid).collect { about ->
                _state.update {
                    it.copy(
                        about = about
                    )
                }
            }

        }
    }


    fun sendMessageViewModel(message: Message) {
        viewModelScope.launch {
            sendMessage(message).collect { isSent ->
                _state.update {
                    it.copy(isSent = isSent)
                }
            }
        }
    }

    fun setIsSentToFalse() {
        viewModelScope.launch {
            _state.update {
                it.copy(isSent = false)
            }
        }
    }

    fun getAllMessagesViewModel() {
        viewModelScope.launch {
            getAllMessages(
                state.value.contactUser?.uid!!,
                state.value.firstTime,
                state.value.position
            )
                .collect { query ->
                    Log.e("TAG", "getAllMessagesViewModel: called" )
                    var position = query!!.documents[query.size()-1].toObject(Message::class.java)
                    var list = emptySet<Message>()
                    if(query.size() > 0) {
                        list = query.toObjects(Message::class.java).toSet()
                    }
                    _state.update {
                        var messages =  it.messages + list
                        if (it.firstTime) {
                            listenForNewMessageViewModel()
                            it.copy(messages = messages, firstTime = false, position = position)
                        } else {

                            it.copy(messages = messages, position = position)
                        }
                    }
                }
        }

    }

    private fun listenForNewMessageViewModel(){
        viewModelScope.launch {
            listenForNewMessage(state.value.contactUser?.uid!!).collect { message ->
                _state.update {
                    var newList  = it.messages + message
                    it.copy(messages = newList)
                }
            }
        }
    }

    private fun emptyNewMessage(){
        viewModelScope.launch {
            _state.update {
                it.copy(newMessage = null)
            }
        }
    }
}