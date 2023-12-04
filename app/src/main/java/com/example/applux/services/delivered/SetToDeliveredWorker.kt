package com.example.applux.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.applux.data.source.message.MessageRepository
import com.example.applux.domain.models.MessageState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
@HiltWorker
class SetToDeliveredWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workParams: WorkerParameters,
    val messageRepo: MessageRepository
) : CoroutineWorker(
    context,
    workParams
) {
    override suspend fun doWork(): Result {
        val senderUID = inputData.getString("senderUID")
        val receiverUID = inputData.getString("receiverUID")
        val messageId = inputData.getString("messageId")

        messageRepo.changeMessageState(
            senderUID!!,
            receiverUID!!,
            messageId!!,
            MessageState.DELIVERED
        )

        return Result.success()
    }
}