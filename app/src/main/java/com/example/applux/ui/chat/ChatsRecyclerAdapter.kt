package com.example.applux.ui.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.applux.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class ChatsRecyclerAdapter @Inject constructor() :
    RecyclerView.Adapter<ChatsRecyclerAdapter.ChatViewHolder>() {

    private var setOfUsers = ArrayList<ChatItemUiState>()

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val contactImage = itemView.findViewById(R.id.contactImage) as ShapeableImageView
        val contactName = itemView.findViewById(R.id.contactName) as MaterialTextView
        val contactLastMsg = itemView.findViewById(R.id.contactLastMessage) as MaterialTextView
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sender_user_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        val chatItemUiState = setOfUsers.elementAt(position)

        if (chatItemUiState.profileBitmap != null) {
            holder.contactImage.setImageBitmap(chatItemUiState.profileBitmap)
        }
        if (chatItemUiState.message != null) {
            holder.contactLastMsg.text = chatItemUiState.message!!.text
        }
        if (chatItemUiState.contactUser != null) {
            holder.contactName.text = chatItemUiState.contactUser!!.name
        }

    }

    override fun getItemCount(): Int {
        return setOfUsers.size
    }


    fun setAllUsers(array: ArrayList<ChatItemUiState>) {
        val diffCallback = diff(setOfUsers, array)
        val diffResult = DiffUtil.calculateDiff(diffCallback)


        setOfUsers.clear()
        setOfUsers.addAll(array)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getChatItem(position: Int): ChatItemUiState {
        return setOfUsers.elementAt(position)
    }

    class diff(
        val oldList: List<ChatItemUiState>,
        val newList: List<ChatItemUiState>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

            /*
                return oldList[oldItemPosition].message!!.equals(newList[newItemPosition].message)
                        && oldList[oldItemPosition].profileBitmap!!.equals(newList[newItemPosition].profileBitmap!!)*/
            return false

        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].equals(newList[newItemPosition])
        }

    }
}