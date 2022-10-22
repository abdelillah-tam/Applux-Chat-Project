package com.example.applux.domain.models

import android.os.Parcelable
import com.example.applux.OnlineOrOffline
import com.example.applux.Privacy
import kotlinx.parcelize.Parcelize

@Parcelize
data class LastSeen(
    var uid: String,
    var timestamp: String?,
    var onlineOrOffline: OnlineOrOffline?,
    var privacy: Privacy
) :Parcelable {

    constructor() : this("","", OnlineOrOffline.OFFLINE, Privacy.PUBLIC)
}