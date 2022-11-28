package com.example.applux.ui.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.applux.R
import com.example.applux.domain.models.Contact
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class CursorRecyclerViewAdapter @Inject constructor() : RecyclerView.Adapter<CursorRecyclerViewAdapter.Holder>() {


    private var contactsUsers = ArrayList<ContactsItemUiState>()

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val contactPic = itemView.findViewById(R.id.circleContactPic) as ShapeableImageView
        val nameTextView = itemView.findViewById(R.id.contact_name_textview) as MaterialTextView
        val numberTextView = itemView.findViewById(R.id.contact_number_textview) as MaterialTextView
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val contact = contactsUsers.get(position)
        val getName = contact.contactUser?.name
        val getNumber = contact.contactUser?.phoneOrEmail


        holder.nameTextView.text = getName
        holder.numberTextView.text = getNumber
        if (contact.picture == null){
            holder.contactPic.setImageResource(R.drawable.ic_face)
        }else{
            Glide.with(holder.itemView.context).load(contact.picture!!.pic).into(holder.contactPic)
        }

    }

    override fun getItemCount(): Int {
        return contactsUsers.size
    }


    fun setContactsFromFirebase(contacts: ArrayList<ContactsItemUiState>){
        contactsUsers = contacts
        notifyDataSetChanged()
    }

    fun getContact(position: Int) : ContactsItemUiState {
        return contactsUsers.get(position)
    }
}

