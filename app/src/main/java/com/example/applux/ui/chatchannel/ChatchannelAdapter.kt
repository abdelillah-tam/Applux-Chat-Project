package com.example.applux.ui.chatchannel

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.applux.R
import com.example.applux.domain.models.Message
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.scopes.FragmentScoped
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@FragmentScoped
class ChatchannelAdapter @Inject constructor() :
    RecyclerView.Adapter<ChatchannelAdapter.MessageViewHolder>() {
    @Inject
    lateinit var auth: FirebaseAuth
    private var array: ArrayList<Message> = ArrayList()
    private val SENDER = 1
    private val RECEIVER = 2

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val msgText = itemView.findViewById(R.id.message_text) as MaterialTextView
        val date = itemView.findViewById(R.id.msg_date) as MaterialTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        if (viewType == SENDER) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.sender_item, parent, false)
            return MessageViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.receiver_item, parent, false)
            return MessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = array.get(position)

        holder.msgText.text = message.text
        val dateString = timestampToDate(message.timestamp!!)
        holder.date.text = dateString
    }


    fun addMessage(message: Message) {

        notifyDataSetChanged()
    }

    fun addMessages(messages: ArrayList<Message>) {
        val diff = Diff(this.array, messages)
        val diffResult = DiffUtil.calculateDiff(diff)

        array.clear()
        array.addAll(messages)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = array.elementAt(position)
        if (message.senderUID == auth.currentUser!!.uid) {
            return SENDER
        } else {
            return RECEIVER
        }
    }

    private fun timestampToDate(timestamp: String): String {
        val date: DateFormat = SimpleDateFormat("hh:mm a dd/MM/yy")
        date.timeZone = TimeZone.getDefault()
        val secondDate = Date(timestamp.toLong() * 1000L)
        return date.format(secondDate)
    }

    class Diff(val oldList: ArrayList<Message>,
        val newList : ArrayList<Message>
               ) : DiffUtil.Callback(){
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].messageId.equals(newList[newItemPosition].messageId)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].equals(newList[newItemPosition])
        }

    }
}