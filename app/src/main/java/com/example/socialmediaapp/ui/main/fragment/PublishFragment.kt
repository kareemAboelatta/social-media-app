package com.example.socialmediaapp.ui.main.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.socialmediaapp.R
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PublishFragment  : Fragment(R.layout.fragment_publish) {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }
}