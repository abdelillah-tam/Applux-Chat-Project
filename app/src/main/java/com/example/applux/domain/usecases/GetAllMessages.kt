package com.example.applux.domain.usecases

import com.example.applux.data.firebase.message.MessageRepository
import com.example.applux.domain.models.Message
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllMessages @Inject constructor(
    private val messageRepository: MessageRepository
) {


    operator fun invoke(
        receiverUid: String,
        firstTime: Boolean,
        position: Message?
    ): Flow<QuerySnapshot?> = flow {

        messageRepository.getAllMessages(receiverUid, firstTime, position).collect {
            //Log.e("TAG", "invoke: " + position?.toObject(Message::class.java)?.text )
            emit(it)
        }


    }

}