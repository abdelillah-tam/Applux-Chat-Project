package com.example.applux.ui.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.domain.models.Message
import com.example.applux.domain.usecases.SendMessage
import com.example.applux.domain.usecases.UploadMessagePicture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val sendMessage: SendMessage,
    private val uploadMessagePicture: UploadMessagePicture
) : ViewModel() {

    private val _isSentState = MutableStateFlow(false)
    val isSentState = _isSentState.asStateFlow()

    fun sendMessageImageViewModel(message: Message, receiverUid: String, bitmap: Bitmap) {
        viewModelScope.launch {
            val baos = ByteArrayOutputStream()

            if (bitmap.byteCount > 500000) {
                bitmap.compress(Bitmap.CompressFormat.WEBP, 90, baos)
            } else bitmap.compress(Bitmap.CompressFormat.WEBP, 100, baos)

            val fileName = UUID.nameUUIDFromBytes(baos.toByteArray()).toString()

            uploadMessagePicture(receiverUid, baos.toByteArray(), fileName).collect {
                if (it != null) {
                    message.imageLink = it.toString()
                    sendMessage(message, receiverUid).collect { result ->
                        if (result) {
                            _isSentState.update {
                                result
                            }
                        } else {
                            _isSentState.update {
                                result
                            }
                        }
                    }
                }
            }
        }
    }

}