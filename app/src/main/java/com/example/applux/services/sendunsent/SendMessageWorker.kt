package com.example.applux.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.applux.data.source.message.MessageRepository
import com.example.applux.data.source.message.local.LocalMessage
import com.example.applux.domain.models.MessageState
import com.example.applux.domain.models.MessageType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@HiltWorker
class SendMessageWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workParams: WorkerParameters,
    val messageRepo: MessageRepository
) : CoroutineWorker(
    context,
    workParams
) {
    override suspend fun doWork(): Result {
        val localMessage = LocalMessage(
            id = inputData.keyValueMap["id"] as Int,
            messageId = inputData.keyValueMap["messageId"] as String,
            timestamp = inputData.keyValueMap["timestamp"] as String,
            text = inputData.keyValueMap["text"] as String,
            receiverUID = inputData.keyValueMap["receiverUID"].toString(),
            senderUID = inputData.keyValueMap["senderUID"].toString(),
            messageType = MessageType.valueOf(inputData.keyValueMap["messageType"].toString()),
            imageLink = inputData.keyValueMap["imageLink"].toString(),
            messageState = MessageState.valueOf(inputData.keyValueMap["messageState"].toString()),
            day = inputData.keyValueMap["day"] as Int,
            dayOfWeek = inputData.keyValueMap["dayOfWeek"] as Int,
            month = inputData.keyValueMap["month"] as Int,
            year = inputData.keyValueMap["year"] as Int
        )
        val result = withContext(Dispatchers.IO) {
            messageRepo
                .sendUnsentMessagesToFirestore(listOf(localMessage))
        }

        if (result) {
            return Result.success(workDataOf("result" to result))
        } else {
            return Result.failure(workDataOf("result" to result))
        }
    }


}