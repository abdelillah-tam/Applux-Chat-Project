package com.example.applux.workers

import android.content.Context
import androidx.work.*
import com.example.applux.data.source.message.local.LocalMessage
import java.util.*
import javax.inject.Inject

class SendUnsentMessagesScheduler @Inject constructor(
    val context: Context
) {

    private val PACKAGE_NAME = "com.example.applux"

    private val uploadConstraints = Constraints
        .Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun sendUnsentMessages(unsentMessages: List<LocalMessage>) {
        //val serviceName = Ser::class.java.name
        //val component = ComponentName(PACKAGE_NAME, serviceName)


        //val inputData = workDataOf("list" to unsentMessages)
        unsentMessages.forEach { localMessage ->

            val messageMap = mapOf(
                "id" to localMessage.id,
                "messageId" to localMessage.messageId,
                "timestamp" to localMessage.timestamp,
                "text" to localMessage.text,
                "receiverUID" to localMessage.receiverUID,
                "senderUID" to localMessage.senderUID,
                "messageType" to localMessage.messageType!!.name,
                "imageLink" to localMessage.imageLink,
                "messageState" to localMessage.messageState!!.name,
                "day" to localMessage.day,
                "dayOfWeek" to localMessage.dayOfWeek,
                "month" to localMessage.month,
                "year" to localMessage.year
            )

            val inputData = Data.Builder()
                .putAll(messageMap)
                .build()


            val uploadWorkReq = OneTimeWorkRequest
                .Builder(SendMessageWorker::class.java)
                .setConstraints(uploadConstraints)
                .setInputData(inputData)
                .build()


            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(
                    localMessage.messageId,
                    ExistingWorkPolicy.REPLACE,
                    uploadWorkReq
                )
        }


    }

}