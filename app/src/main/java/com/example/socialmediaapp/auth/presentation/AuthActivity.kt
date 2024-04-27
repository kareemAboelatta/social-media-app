package com.example.socialmediaapp.auth.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.socialmediaapp.R
import com.example.socialmediaapp.ui.main.ViewModelMain
import com.example.socialmediaapp.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: FirebaseAuth


    private val viewModel by viewModels<ViewModelMain>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        checkUserState()







    }


    private fun checkUserState(){
        if (auth.currentUser != null ){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}