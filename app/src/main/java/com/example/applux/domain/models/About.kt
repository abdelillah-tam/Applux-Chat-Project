package com.example.applux.domain.models

import android.os.Parcelable
import com.example.applux.Privacy
import kotlinx.android.parcel.Parcelize

@Parcelize
data class About(var about: String, var privacy: Privacy) : Parcelable{

    constructor() : this("", Privacy.PUBLIC)
}