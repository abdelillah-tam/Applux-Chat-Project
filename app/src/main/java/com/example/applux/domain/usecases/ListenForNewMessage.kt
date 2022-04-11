package com.example.applux.domain.usecases

import com.example.applux.data.firebase.message.MessageRepository
import com.example.applux.domain.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ListenForNewMessage @Inject constructor(
    private val messageRepository: MessageRepository
) {


    operator fun invoke(receiverUid: String) : Flow<Message> = flow {
        messageRepository.listenForNewMessage(receiverUid).collect {
            emit(it!!)
        }
    }

}