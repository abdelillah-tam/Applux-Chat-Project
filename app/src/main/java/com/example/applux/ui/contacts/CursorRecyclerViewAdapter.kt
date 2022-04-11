package com.example.applux.ui.contacts


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applux.R
import com.example.applux.domain.models.ContactUser
import dagger.hilt.android.scopes.FragmentScoped
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject

@FragmentScoped
class CursorRecyclerViewAdapter @Inject constructor() : RecyclerView.Adapter<CursorRecyclerViewAdapter.Holder>() {


    private var contactsUsers = ArrayList<ContactsItemUiState>()

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val contact = contactsUsers.get(position)
        val getName = contact.contactUser?.name
        val getNumber = contact.contactUser?.phone

        val contactPic = holder.itemView.findViewById(R.id.circleContactPic) as CircleImageView
        val nameTextView = holder.itemView.findViewById(R.id.contact_name_textview) as TextView
        val numberTextView = holder.itemView.findViewById(R.id.contact_number_textview) as TextView

        nameTextView.text = getName
        numberTextView.text = getNumber
        if (contact.profileBitmap == null){
            contactPic.setImageResource(R.drawable.ic_face)
        }else{
            contactPic.setImageBitmap(contact.profileBitmap)
        }

    }

    override fun getItemCount(): Int {
        return contactsUsers.size
    }

    fun setContactsFromFirebase(contacts: ArrayList<ContactsItemUiState>){
        contactsUsers = contacts
        notifyDataSetChanged()
    }

   /* fun getContact(position: Int) : ContactUser{
        return contactsUsers.get(position)
    }*/
}

