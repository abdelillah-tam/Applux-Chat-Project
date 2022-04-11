package com.example.applux.domain.models

import com.example.applux.OnlineOrOffline
import com.example.applux.Privacy

data class LastSeen(
    var timestamp: String?,
    var onlineOrOffline: OnlineOrOffline?,
    var privacy: Privacy
) {

    constructor() : this("", OnlineOrOffline.OFFLINE, Privacy.PUBLIC)
}