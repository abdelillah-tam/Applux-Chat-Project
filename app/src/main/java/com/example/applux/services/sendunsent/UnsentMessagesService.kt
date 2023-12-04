package com.example.applux.workers

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.example.applux.data.source.message.MessageRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
class UnsentMessagesService : LifecycleService() {

    @Inject
    lateinit var messageRepo: MessageRepository

    @Inject
    lateinit var sendUnsentMessagesScheduler: SendUnsentMessagesScheduler

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (Firebase.auth.currentUser != null) {
            thread {
                CoroutineScope(Dispatchers.IO).launch {
                    messageRepo
                        .getUnsentMessages()
                        .collect { localMessages ->
                            sendUnsentMessagesScheduler.sendUnsentMessages(localMessages)
                        }
                }

            }
        }


        return START_STICKY
    }
}