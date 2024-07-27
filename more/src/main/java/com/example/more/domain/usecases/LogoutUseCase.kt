package com.example.more.domain.usecases

import com.example.common.data.local.UserPreferences
import com.example.common.domain.model.User
import com.example.core.ui.utils.DataState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userPreferences: UserPreferences,
    private val auth: FirebaseAuth
) {

    suspend operator fun invoke(): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        val job = CoroutineScope(Dispatchers.IO).async {
            auth.signOut()
            userPreferences.deleteUserData()
        }
        job.await()
        emit(DataState.Success(Unit))
    }
}