package com.domain.usecases

import com.domain.models.CreateUserInput
import com.domain.repository.AuthRepository
import com.example.common.domain.model.User
import javax.inject.Inject

class CreateUser @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(createUserInput: CreateUserInput): User =
        repository.createUser(createUserInput)

}