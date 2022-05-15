package com.example.socialmediaapp.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

object MyFirebase {

    val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

}