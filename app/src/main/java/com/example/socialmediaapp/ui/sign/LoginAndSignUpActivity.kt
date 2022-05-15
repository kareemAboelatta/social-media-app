package com.example.socialmediaapp.ui.sign

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
class LoginAndSignUpActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: FirebaseAuth


    private val viewModel by viewModels<ViewModelMain>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_and_sign_up)
        checkUserState()







    }


    private fun checkUserState(){
        if (auth.currentUser != null ){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}