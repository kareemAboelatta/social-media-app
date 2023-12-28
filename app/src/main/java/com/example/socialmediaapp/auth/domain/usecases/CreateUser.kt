package com.example.socialmediaapp.auth.domain.usecases

import com.example.socialmediaapp.auth.domain.models.CreateUserInput
import com.example.socialmediaapp.auth.domain.repository.AuthRepository
import com.example.socialmediaapp.models.User
import javax.inject.Inject

class CreateUser @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(createUserInput: CreateUserInput): User =
        repository.createUser(createUserInput)

}