package com.example.applux.ui.chat

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import javax.inject.Inject

class ChatsRecyclerAdapter @Inject constructor() :
    RecyclerView.Adapter<ChatsRecyclerAdapter.ChatViewHolder>() {

    private var setOfUsers = ArrayList<ChatItemUiState>()

    private var resources : Resources? = null

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        /*val contactImage = itemView.findViewById(R.id.contactImage) as ShapeableImageView
        val contactName = itemView.findViewById(R.id.contactName) as MaterialTextView
        val contactLastMsg = itemView.findViewById(R.id.contactLastMessage) as MaterialTextView
        val isOnline = itemView.findViewById(R.id.is_online) as MaterialCheckBox*/
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.sender_user_item, parent, false)
        return ChatViewHolder(parent.rootView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        val chatItemUiState = setOfUsers.elementAt(position)

        if (chatItemUiState.picture != null && chatItemUiState.picture!!.pic.isNotEmpty()){
           // Glide.with(holder.itemView.context).load(chatItemUiState.picture!!.pic).into(holder.contactImage)
        }else{
          //  Glide.with(holder.itemView.context).load(R.drawable.ic_face).into(holder.contactImage)
        }

        if (chatItemUiState.message != null) {
            //holder.contactLastMsg.text = chatItemUiState.message!!.text
        }
        if (chatItemUiState.contactUser != null) {
            //holder.contactName.text = chatItemUiState.contactUser!!.name
        }

        if (chatItemUiState.lastSeen != null){
           // holder.isOnline.isChecked = chatItemUiState.lastSeen!!.onlineOrOffline!!.equals(OnlineOrOffline.ONLINE)
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

    fun setResources(resources: Resources)
    {
        this.resources = resources
    }
}