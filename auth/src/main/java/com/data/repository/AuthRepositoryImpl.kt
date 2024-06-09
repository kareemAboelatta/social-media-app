package com.data.repository


import com.data.datasource.AuthDatasource
import com.domain.models.CreateUserInput
import com.domain.repository.AuthRepository
import com.example.common.domain.model.User
import com.example.core.data.remote.safeFirebaseCall
import com.example.core.ui.utils.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private var authDatasource: AuthDatasource,
) : AuthRepository {

    override suspend fun createUser(userInput: CreateUserInput): Flow<DataState<User>> =
        safeFirebaseCall {
            authDatasource.createUser(userInput)
        }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<DataState<User>> =
        safeFirebaseCall { authDatasource.signInWithEmailAndPassword(email, password) }

    override suspend fun resetPassword(email: String): Flow<DataState<Boolean>> = safeFirebaseCall {
        authDatasource.resetPassword(email)
    }


    // Other private utility methods...
}
