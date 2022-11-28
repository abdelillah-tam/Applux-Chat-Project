package com.example.applux.ui.contacts

import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.domain.usecases.DownloadProfilePicture
import com.example.applux.domain.usecases.FindContactUser
import com.example.applux.domain.usecases.GetProfilePicture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val findContactUser: FindContactUser,
    private val getProfilePicture: GetProfilePicture
) : ViewModel() {

    private val _state = MutableStateFlow(ContactsUiState())
    val state = _state.asStateFlow()


    fun getContactsViewModel(contacts: HashMap<String, String>) {
        viewModelScope.launch {
            findContactUser(contacts).collect {
                val list = ArrayList<ContactsItemUiState>()
                var position : Int
                it.forEach {
                    val item = ContactsItemUiState(contactUser = it)
                    list.also {
                        it.add(item)
                        position = it.indexOf(item)
                    }
                    getProfilePictureViewModel(it.uid!!, position)
                }
                _state.update {
                    it.copy(contactsItemUiState = list)
                }
            }
        }

    }

    private fun getProfilePictureViewModel(uid: String, positionInList: Int) {
        viewModelScope.launch {
            getProfilePicture(uid).collect { profilePicture ->
                if (profilePicture != null){
                    _state.update {
                        val list = ArrayList<ContactsItemUiState>()
                        list.addAll(it.contactsItemUiState)
                        val item = ContactsItemUiState(
                            picture = profilePicture,
                            contactUser = list.get(positionInList).contactUser,
                            profileBitmap = list.get(positionInList).profileBitmap
                        )
                        list.set(positionInList, item)
                        it.copy(contactsItemUiState = list)
                    }
                }
            }

        }
    }




   /* downloadProfilePicture(
    uid,
    profilePicture.pic
    ).collect { byteArray ->
        _state.update {
            val list = ArrayList<ContactsItemUiState>()
            list.addAll(it.contactsItemUiState)
            val item = ContactsItemUiState(
                picture = list.get(positionInList).picture,
                contactUser = list.get(positionInList).contactUser,
                profileBitmap = list.get(positionInList).profileBitmap
            )

            if (byteArray != null) {
                item.profileBitmap =
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            } else {
                item.profileBitmap = null
            }

            list.set(positionInList, item)
            it.copy(contactsItemUiState = list)
        }
    }*/
}