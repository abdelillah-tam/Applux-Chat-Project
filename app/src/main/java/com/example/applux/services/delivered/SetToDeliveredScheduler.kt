package com.example.applux.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.applux.data.source.message.local.LocalMessage
import javax.inject.Inject

class SetToDeliveredScheduler @Inject constructor(
    val context: Context
) {


    private val uploadConstraints = Constraints
        .Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun setToDelivered(receivedMessages: List<LocalMessage>) {

        receivedMessages.forEach { localMessage ->
            val senderUID = localMessage.senderUID
            val receiverUID = localMessage.receiverUID
            val messageId = localMessage.messageId

            val input = Data.Builder()
                .putString("senderUID", senderUID)
                .putString("receiverUID", receiverUID)
                .putString("messageId", messageId)
                .build()

            val req = OneTimeWorkRequest
                .Builder(SetToDeliveredWorker::class.java)
                .setConstraints(uploadConstraints)
                .setInputData(input)
                .build()

            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(
                    messageId,
                    ExistingWorkPolicy.REPLACE,
                    req
                )
        }

    }

}