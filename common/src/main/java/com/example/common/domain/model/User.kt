package com.example.common.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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