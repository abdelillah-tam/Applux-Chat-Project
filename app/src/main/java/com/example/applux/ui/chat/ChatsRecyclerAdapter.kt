package com.example.applux.ui.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applux.R
import dagger.hilt.android.scopes.FragmentScoped
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@FragmentScoped
class ChatsRecyclerAdapter @Inject constructor() : RecyclerView.Adapter<ChatsRecyclerAdapter.ChatViewHolder>(){

    private var setOfUsers = ArrayList<ChatItemUiState>()
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sender_user_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val contactImage = holder.itemView.findViewById(R.id.contactImage) as CircleImageView
        val contactName = holder.itemView.findViewById(R.id.contactName) as TextView
        val contactLastMsg = holder.itemView.findViewById(R.id.contactLastMessage) as TextView

        val chatItemUiState = setOfUsers.elementAt(position)

        if (chatItemUiState.profileBitmap != null) {
            contactImage.setImageBitmap(chatItemUiState.profileBitmap)
        }
        if (chatItemUiState.message != null) {
            contactLastMsg.text = chatItemUiState.message!!.text
        }
        if (chatItemUiState.contactUser != null) {
            contactName.text = chatItemUiState.contactUser!!.name
        }

    }

    override fun getItemCount(): Int {
        return setOfUsers.size
    }



    fun setAllUsers(array: ArrayList<ChatItemUiState>, newUpdate: ChatItemUiState?){
        GlobalScope.launch(Dispatchers.IO) {
            if (!array.isEmpty() && newUpdate == null) {
                setOfUsers = array
                withContext(Dispatchers.Main) {
                    notifyDataSetChanged()
                }
            } else if (!array.isEmpty() && newUpdate != null) {
                setOfUsers = array
                val position = setOfUsers.indexOf(newUpdate)
                withContext(Dispatchers.Main) {
                    notifyItemChanged(position)
                }
            }
        }
    }

    fun getChatItem(position: Int) : ChatItemUiState{
        return setOfUsers.elementAt(position)
    }


}