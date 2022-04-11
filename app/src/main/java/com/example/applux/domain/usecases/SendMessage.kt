package com.example.applux.domain.usecases

import com.example.applux.data.firebase.message.MessageRepository
import com.example.applux.domain.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class SendMessage @Inject constructor(
    private val messageRepository: MessageRepository
){


    operator fun invoke(message: Message) : Flow<Boolean> = flow{
        val isSent = messageRepository.sendMessage(message)
        emit(isSent)
    }


}