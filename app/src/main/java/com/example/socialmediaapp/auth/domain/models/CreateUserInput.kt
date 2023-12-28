package com.example.socialmediaapp.auth.domain.models

import android.net.Uri

data class CreateUserInput(
    val email:String,
    val password:String,
    val name:String,
    val bio:String,
    val image:Uri,
)