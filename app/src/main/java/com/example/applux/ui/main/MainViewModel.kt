package com.example.applux.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.applux.OnlineOrOffline
import com.example.applux.domain.usecases.UpdateLastSeen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val updateLastSeen : UpdateLastSeen
) : ViewModel(){

    fun updateLastSeenViewModel(timestamp: String, onlineOrOffline: OnlineOrOffline){
        viewModelScope.launch {
            updateLastSeen(timestamp, onlineOrOffline)
        }
    }

}