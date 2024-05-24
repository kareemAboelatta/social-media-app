package com.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.auth.R
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        checkUserState()
    }


    private fun checkUserState(){
        if (auth.currentUser != null ){
//            openMainActivity()
        }
    }
}