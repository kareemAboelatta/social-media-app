package com.example.common.domain.repository

import com.example.common.domain.model.User
import com.example.common.ui.utils.Resource
import com.example.core.ui.utils.DataState
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserInfo(userId:String): Flow<DataState<User>>
    suspend fun getCurrentUserInfo(): Flow<DataState<User>>
}