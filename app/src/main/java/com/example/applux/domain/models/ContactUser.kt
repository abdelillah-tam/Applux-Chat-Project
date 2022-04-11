package com.example.applux.domain.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactUser(
    var uid: String?,
    var phone: String?,
    var name: String?
) : Parcelable {

    constructor() : this("", "", "")
}
