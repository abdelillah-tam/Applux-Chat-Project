package com.example.applux.ui.chatchannel

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.applux.R
import com.example.applux.data.TimeByZoneClient
import com.example.applux.data.WorldTimeModel
import com.example.applux.domain.models.Message
import com.example.applux.domain.models.MessageType
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.scopes.FragmentScoped
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    private var array: ArrayList<MessageUiState> = ArrayList()
    private val SENDER = 1
    private val RECEIVER = 2

    private var lastDateValue: String = ""

    private var startDayOfToday : Long = 0L
    private var startDayOfYesterday : Long = 0L

    private val time : Call<WorldTimeModel> = TimeByZoneClient.getTime(DateTimeZone.getDefault().id)

    init {
        time.enqueue(object : Callback<WorldTimeModel>{
            override fun onResponse(
                call: Call<WorldTimeModel>,
                response: Response<WorldTimeModel>
            ) {
                val byZone = response.body()!!
                startDayOfToday = DateTime.parse(byZone.dateTime).withTimeAtStartOfDay().millis / 1000L
                startDayOfYesterday = DateTime.parse(byZone.dateTime).minusDays(1).withTimeAtStartOfDay().millis / 1000L
            }

            override fun onFailure(call: Call<WorldTimeModel>, t: Throwable) {
            }

        })
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date = itemView.findViewById(R.id.date) as MaterialTextView
        val msgImage = itemView.findViewById(R.id.message_image) as ShapeableImageView
        val msgText = itemView.findViewById(R.id.message_text) as MaterialTextView
        val msgDate = itemView.findViewById(R.id.msg_date) as MaterialTextView
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
        val messageUiState = array.get(position)

        val result = calculateDate(messageUiState.message.timestamp!!.toLong())

        if (lastDateValue.equals(result)) {
            holder.date.visibility = MaterialTextView.GONE
        } else {
            holder.date.text = result
            holder.date.visibility = MaterialTextView.VISIBLE
            lastDateValue = result
        }

        if (messageUiState.message.messageType!!.equals(MessageType.IMAGE) && messageUiState.bitmap != null){
            //Glide.with(holder.itemView.context).load(messageUiState.message.imageLink).into(holder.msgImage)
            holder.msgImage.setImageBitmap(messageUiState.bitmap)
            holder.msgImage.visibility = ShapeableImageView.VISIBLE
        }else{
            holder.msgImage.visibility = ShapeableImageView.GONE
        }


        holder.msgText.text = messageUiState.message.text
        val dateString = timestampToHoursAndMinutes(messageUiState.message.timestamp!!)
        holder.msgDate.text = dateString

    }


    fun addMessages(messages: ArrayList<MessageUiState>) {
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
        val messageUiState = array.elementAt(position)
        if (messageUiState.message.senderUID == auth.currentUser!!.uid) {
            return SENDER
        } else {
            return RECEIVER
        }
    }


    private fun timestampToHoursAndMinutes(timestamp: String): String {
        val date: DateFormat = SimpleDateFormat("hh:mm a")
        date.timeZone = TimeZone.getDefault()
        val secondDate = Date(timestamp.toLong() * 1000L)
        return date.format(secondDate)
    }

    private fun timestampToDaysAndMonth(timestamp: Long): String {
        val date: DateFormat = SimpleDateFormat("dd MMMM")
        date.timeZone = TimeZone.getDefault()
        val secondDate = Date(timestamp * 1000L)
        return date.format(secondDate)
    }

    private fun timestampToDaysAndMonthAndYears(timestamp: Long): String {
        val date: DateFormat = SimpleDateFormat("dd MMMM yyyy")
        date.timeZone = TimeZone.getDefault()
        val secondDate = Date(timestamp * 1000L)
        return date.format(secondDate)
    }

    private fun calculateDate(timestamp: Long): String {
        val oneDayInSeconds = 24 * 60 * 60
        val oneYearInSeconds = (oneDayInSeconds * 365)
        val currentTime = System.currentTimeMillis() / 1000L


        if (timestamp < startDayOfToday && timestamp >= startDayOfYesterday
        ) {
            return "Yesterday"
        } else if (timestamp < startDayOfYesterday) {
            return timestampToDaysAndMonth(timestamp)
        } else if (currentTime >= (timestamp + oneYearInSeconds)) {
            return timestampToDaysAndMonthAndYears(timestamp)
        } else {
            return "Today"
        }
    }

    class Diff(
        val oldList: ArrayList<MessageUiState>,
        val newList: ArrayList<MessageUiState>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

            return oldList[oldItemPosition].message.messageId.equals(newList[newItemPosition].message.messageId)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].equals(newList[newItemPosition])
        }

    }
}