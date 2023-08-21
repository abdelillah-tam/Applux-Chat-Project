package com.example.applux.ui.chatchannel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.domain.models.ContactUser
import com.example.applux.domain.models.Message
import com.example.applux.domain.models.MessageType
import com.example.applux.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatChannelViewModel @Inject constructor(
    private val getAbout: GetAbout,
    private val sendMessage: SendMessage,
    private val getContact: GetContact,
    private val getProfilePicture: GetProfilePicture,
    private val listenForNewMessage: ListenForNewMessage,
    private val userState: UserState
) : ViewModel() {


    private val _state = MutableStateFlow(ChatChannelUiState())
    val state = _state.asStateFlow()


    fun getUserDataViewModel(
        pictureBitmap: Bitmap?,
        uid: String
    ) {
        viewModelScope.launch {

            var contactUser: ContactUser? = ContactUser()
            withContext(Dispatchers.IO) {
                getContact(uid).collect {
                    contactUser = it
                }
            }

            _state.update {
                it.copy(
                    contactUser = contactUser
                )
            }
            if (pictureBitmap == null) {
                getProfilePicture(contactUser!!.uid!!).collect { picture ->
                    if (picture != null) {
                        withContext(Dispatchers.IO) {
                            //bitmap = Glide.with(context).asBitmap().load(it.pic).submit().get()
                        }
                        _state.update {
                            it.copy(profilePicture = picture.pic)
                        }
                    }
                }
            }

            getAboutViewModel(contactUser!!.uid!!)
            userStateViewModel(contactUser!!.uid!!)
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


    fun sendMessageViewModel(message: Message, receiverUid: String) {
        viewModelScope.launch {
            sendMessage(message, receiverUid).collect { isSent ->
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

    fun listenForNewMessageViewModel() {
        viewModelScope.launch {
            listenForNewMessage(state.value.contactUser?.uid!!).collect { messages ->

                messages.forEach { message ->
                    if (message.messageType!! == MessageType.IMAGE) {
                        val messageUiState = MessageUiState(message, null)
                        getMessageImage(message.imageLink!!).collect { bitmap ->
                            _state.update {
                                val hs = HashMap(it.messages)
                                messageUiState.bitmap = bitmap
                                hs.put(message.messageId, messageUiState)
                                it.copy(messages = hs)
                            }
                        }

                    } else {
                        val messageUiState = MessageUiState(message, null)
                        _state.update {
                            val hs = HashMap(it.messages)
                            hs.put(message.messageId, messageUiState)
                            it.copy(messages = hs)
                        }
                    }

                }

            }
        }
    }

    private fun emptyNewMessage() {
        viewModelScope.launch {
            _state.update {
                it.copy(newMessage = null)
            }
        }
    }

    private fun userStateViewModel(uid: String) {
        viewModelScope.launch {
            userState(uid).collect { lastSeen ->
                _state.update {
                    it.copy(lastSeen = lastSeen)
                }
            }
        }
    }

    private fun getMessageImage(imageLink: String): Flow<Bitmap> = flow {

        val bitmap = withContext(Dispatchers.IO) {
            //Glide.with(context).asBitmap().load(imageLink).submit().get()
        }
        //emit(bitmap)

    }
}