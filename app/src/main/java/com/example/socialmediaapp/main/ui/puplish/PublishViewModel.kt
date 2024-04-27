package com.example.socialmediaapp.main.ui.puplish

import androidx.lifecycle.ViewModel
import com.example.socialmediaapp.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PublishViewModel  @Inject constructor(
    private val repository: Repository,
    val auth: FirebaseAuth,
) : ViewModel() {


}
