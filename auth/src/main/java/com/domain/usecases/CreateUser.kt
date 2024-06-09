package com.domain.usecases

import com.domain.models.CreateUserInput
import com.domain.repository.AuthRepository
import com.example.common.domain.model.User
import com.example.core.ui.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateUser @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(createUserInput: CreateUserInput): Flow<DataState<User>> = flow {

        repository.createUser(createUserInput)
    }

}