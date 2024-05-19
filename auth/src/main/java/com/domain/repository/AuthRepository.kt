package com.domain.repository

import com.domain.models.CreateUserInput
import com.example.common.domain.model.User

interface AuthRepository {
    suspend fun createUser(userInput: CreateUserInput): User
    suspend fun signInWithEmailAndPassword(email: String, password: String): User
    suspend fun resetPassword(email: String):Boolean


}
