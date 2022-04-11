package com.example.applux.domain.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Page(
    val pageId: String,
    val name: String,
    val followersNumber: Int,
    val adminUid: String,
    val lastPost: String,
    val timestampOfLastPost: String
) : Parcelable{
    constructor() : this("","",0,"","","")


    override fun equals(other: Any?): Boolean {
        if(javaClass != other?.javaClass){
            return false
        }

        other as Page
        if (pageId == other.pageId){
            return true
        }

        if (name == other.name){
            return true
        }

        return false
    }


    override fun hashCode(): Int {
        return Math.random().toInt() + 2
    }
}
