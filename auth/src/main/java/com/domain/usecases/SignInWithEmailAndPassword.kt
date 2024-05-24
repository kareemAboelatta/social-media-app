package com.domain.usecases

import com.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailAndPassword @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email:String, password: String)=
        repository.signInWithEmailAndPassword(email, password)

}