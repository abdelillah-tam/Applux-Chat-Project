package com.example.applux.workers

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.example.applux.data.source.message.local.MessageDao
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
class DeliveredMessagesService : LifecycleService() {

    @Inject
    lateinit var messageDao: MessageDao

    @Inject
    lateinit var setToDeliveredScheduler: SetToDeliveredScheduler
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (Firebase.auth.currentUser != null) {

            thread {
                CoroutineScope(Dispatchers.IO).launch{
                    messageDao
                        .getReceivedMessages(Firebase.auth.currentUser!!.uid)
                        .collect {
                            setToDeliveredScheduler.setToDelivered(it)
                        }
                }

            }

        }
        return START_STICKY
    }

}