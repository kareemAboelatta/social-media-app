package com.example.socialmediaapp.ui.sign.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.socialmediaapp.R
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_reset_password.*
import javax.inject.Inject

@AndroidEntryPoint
class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reset_btn_reset.setOnClickListener {
            auth.sendPasswordResetEmail(reset_email.text.toString()).addOnSuccessListener {
                Toast.makeText(context, "Check your email now ..", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, ""+it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }
}