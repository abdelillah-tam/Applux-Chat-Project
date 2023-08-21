package com.example.applux.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.data.firebase.contactuser.ContactUserRepository
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
    private val getProfilePicture: GetProfilePicture,
    private val contactRepository: ContactUserRepository
) : ViewModel() {

    private val _onlineContactState = MutableStateFlow(ContactsUiState())
    val onlineContactState = _onlineContactState.asStateFlow()

    private val _localContactState = MutableStateFlow(ContactsUiState())
    val localContactState = _localContactState.asStateFlow()


    fun getContactsViewModel(contacts: HashMap<String, String>) {
        viewModelScope.launch {
            findContactUser(contacts).collect { contactArray ->
                val list = ArrayList<ContactsItemUiState>()
                var position : Int
                contactArray.forEach {
                    val item = ContactsItemUiState(contactUser = it)
                    list.also { listItem ->
                        listItem.add(item)
                        position = listItem.indexOf(item)
                    }
                    getLocalProfilePictureViewModel(it.uid!!, position)
                }
                _localContactState.update {
                    it.copy(contactsItemUiState = list)
                }
            }
        }

    }

    private fun getOnlineProfilePictureViewModel(uid: String, positionInList: Int) {
        viewModelScope.launch {
            getProfilePicture(uid).collect { profilePicture ->
                if (profilePicture != null){
                    _onlineContactState.update {
                        val list = ArrayList<ContactsItemUiState>()
                        list.addAll(it.contactsItemUiState)
                        val item = ContactsItemUiState(
                            picture = profilePicture,
                            contactUser = list[positionInList].contactUser,
                            profileBitmap = list[positionInList].profileBitmap
                        )
                        list[positionInList] = item
                        it.copy(contactsItemUiState = list)
                    }
                }
            }

        }
    }

    private fun getLocalProfilePictureViewModel(uid: String, positionInList: Int) {
        viewModelScope.launch {
            getProfilePicture(uid).collect { profilePicture ->
                if (profilePicture != null){
                    _localContactState.update {
                        val list = ArrayList<ContactsItemUiState>()
                        list.addAll(it.contactsItemUiState)
                        val item = ContactsItemUiState(
                            picture = profilePicture,
                            contactUser = list[positionInList].contactUser,
                            profileBitmap = list[positionInList].profileBitmap
                        )
                        list[positionInList] = item
                        it.copy(contactsItemUiState = list)
                    }
                }
            }

        }
    }


    fun findUser(name: String){
        viewModelScope.launch {
            contactRepository.findUser(name).collect{ contactArray ->
                val list = ArrayList<ContactsItemUiState>()
                var position : Int
                contactArray.forEach {
                    val item = ContactsItemUiState(contactUser = it)
                    list.also { listItem ->
                        listItem.add(item)
                        position = listItem.indexOf(item)
                    }
                    getOnlineProfilePictureViewModel(it.uid!!, position)
                }
                _onlineContactState.update {
                    ContactsUiState(list)
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