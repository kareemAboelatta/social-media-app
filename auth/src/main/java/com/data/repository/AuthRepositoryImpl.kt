package com.data.repository


import com.data.datasource.AuthDatasource
import com.domain.models.CreateUserInput
import com.domain.repository.AuthRepository
import com.example.common.domain.model.User
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private var authDatasource: AuthDatasource,
) : AuthRepository {

    override suspend fun createUser(userInput: CreateUserInput): User =
        authDatasource.createUser(userInput)

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): User = authDatasource.signInWithEmailAndPassword(email, password)

    override suspend fun resetPassword(email: String): Boolean = authDatasource.resetPassword(email)


    // Other private utility methods...
}
