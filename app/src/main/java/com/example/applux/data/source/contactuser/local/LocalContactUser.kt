package com.example.applux.data.source.contactuser.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactUser(
    var uid: String?,
    var phoneOrEmail: String?,
    var name: String?
) : Parcelable {

    constructor() : this("", "", "")
}
