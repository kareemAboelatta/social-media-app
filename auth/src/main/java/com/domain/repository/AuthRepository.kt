package com.domain.repository

import com.domain.models.CreateUserInput
import com.example.common.domain.model.User
import com.example.core.ui.utils.DataState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun createUser(userInput: CreateUserInput): Flow<DataState<User>>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Flow<DataState<User>>
    suspend fun resetPassword(email: String): Flow<DataState<Boolean>>


}
