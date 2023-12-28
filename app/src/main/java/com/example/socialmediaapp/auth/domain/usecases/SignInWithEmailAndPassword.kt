package com.example.socialmediaapp.auth.domain.usecases

import com.example.socialmediaapp.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailAndPassword @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email:String, password: String){
        repository.signInWithEmailAndPassword(email, password)
    }
}