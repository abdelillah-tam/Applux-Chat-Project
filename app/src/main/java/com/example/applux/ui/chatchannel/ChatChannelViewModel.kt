package com.example.applux.ui.chatchannel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.domain.models.ContactUser
import com.example.applux.domain.models.LastSeen
import com.example.applux.domain.models.Message
import com.example.applux.domain.usecases.*
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
    private val listenForNewMessage: ListenForNewMessage,
    private val userState: UserState
) : ViewModel() {


    private val _state = MutableStateFlow(ChatChannelUiState())
    val state = _state.asStateFlow()



    fun getUserDataViewModel(
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
            userStateViewModel(contactUser.uid!!)
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
                .collect {
                        query ->
                    Log.e("TAG", "getAllMessagesViewModel: called" )
                    val position = query!!.documents[query.size()-1].toObject(Message::class.java)
                    val list = HashMap<String,Message>()
                    if(query.size() > 0) {
                        query.toObjects(Message::class.java).forEach {
                            list.put(it.messageId, it)
                        }
                    }


                    _state.update {
                        val hs = HashMap(it.messages)
                        hs.putAll(list)

                        if (it.firstTime) {
                            listenForNewMessageViewModel()
                            ChatChannelUiState(it.profilePictureBitmap,
                            it.contactUser,
                            list,
                            it.about,
                            it.lastSeen,
                            it.isSent,
                            false,
                            position,
                            it.newMessage)
                        } else if(!hs.equals(it.messages)){
                            it.copy(messages = hs)
                        }else{
                            it.disableLoading = false
                            it.copy(disableLoading = true)
                        }
                    }
                }
        }

    }

    private fun listenForNewMessageViewModel(){
        viewModelScope.launch {
            listenForNewMessage(state.value.contactUser?.uid!!).collect { message ->
                _state.update {
                    val hs = HashMap(it.messages)
                    hs.put(message.messageId, message)
                    it.copy(messages = hs)
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

    private fun userStateViewModel(uid: String){
        viewModelScope.launch {
            userState(uid).collect{ lastSeen ->
                _state.update {
                    it.copy(lastSeen = lastSeen)
                }
            }
        }
    }
}