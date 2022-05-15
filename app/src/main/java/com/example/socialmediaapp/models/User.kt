package com.example.socialmediaapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var name:String="",
    var bio:String="",
    var email:String="",
    var id:String="",
    var image:String="",
    var cover:String="",
    var token: String = "",
    val status: String = ""
): Parcelable