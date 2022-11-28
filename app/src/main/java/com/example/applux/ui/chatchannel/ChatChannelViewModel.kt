package com.example.applux.ui.chatchannel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.applux.domain.models.ContactUser
import com.example.applux.domain.models.Message
import com.example.applux.domain.models.MessageType
import com.example.applux.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatChannelViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val getAbout: GetAbout,
    private val sendMessage: SendMessage,
    private val getAllMessages: GetAllMessages,
    private val getProfilePicture: GetProfilePicture,
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
            var bitmap: Bitmap? = null
            _state.update {
                it.copy(
                    contactUser = contactUser
                )
            }
            if (pictureBitmap == null) {
                getProfilePicture(contactUser.uid!!).collect {
                    if (it != null) {
                        withContext(Dispatchers.IO) {
                            bitmap = Glide.with(context).asBitmap().load(it.pic).submit().get()
                        }
                        _state.update {
                            it.copy(profilePictureBitmap = bitmap)
                        }
                    }
                }
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
                .collect { query ->
                    Log.e("TAG", "getAllMessagesViewModel: called")
                    if (query != null) {
                        val position =
                            query!!.documents[query.size() - 1].toObject(Message::class.java)
                        val list = HashMap<String, MessageUiState>()
                        if (query.size() > 0) {
                            query.toObjects(Message::class.java).forEach { message ->
                                if (message.messageType!!.equals(MessageType.IMAGE)) {
                                    getMessageImage(message.imageLink!!).collect { bitmap ->
                                        list.put(message.messageId, MessageUiState(message, bitmap))
                                    }
                                } else {
                                    list.put(message.messageId, MessageUiState(message, null))
                                }
                            }
                        }

                        _state.update {
                            val hs = HashMap(it.messages)
                            hs.putAll(list)

                            if (it.firstTime) {
                                listenForNewMessageViewModel()
                                ChatChannelUiState(
                                    it.profilePictureBitmap,
                                    it.contactUser,
                                    list,
                                    it.about,
                                    it.lastSeen,
                                    it.isSent,
                                    false,
                                    position,
                                    it.newMessage
                                )
                            } else if (!hs.equals(it.messages)) {
                                it.copy(messages = hs)
                            } else {
                                it.disableLoading = false
                                it.copy(disableLoading = true)
                            }
                        }
                    }else{
                        _state.update {
                            it.disableLoading = false
                            it.copy(disableLoading = true)
                        }
                    }
                }
        }

    }

    private fun listenForNewMessageViewModel() {
        viewModelScope.launch {
            listenForNewMessage(state.value.contactUser?.uid!!).collect { message ->

                if (message.messageType!!.equals(MessageType.IMAGE)) {
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
            Glide.with(context).asBitmap().load(imageLink).submit().get()
        }
        emit(bitmap)

    }
}