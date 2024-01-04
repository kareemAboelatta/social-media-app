package com.example.socialmediaapp.auth.domain.repository

import com.example.socialmediaapp.auth.domain.models.CreateUserInput
import com.example.socialmediaapp.models.User

interface AuthRepository {
    suspend fun createUser(userInput: CreateUserInput): User
    suspend fun signInWithEmailAndPassword(email: String, password: String): User
    suspend fun resetPassword(email: String):Boolean


}
