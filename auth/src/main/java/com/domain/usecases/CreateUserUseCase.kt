package com.domain.usecases

import com.domain.models.CreateUserInput
import com.domain.repository.AuthRepository
import com.example.common.domain.model.User
import com.example.core.domain.utils.ValidationException
import com.example.core.ui.utils.DataState
import com.example.core.utils.isValidEmail
import com.example.core.utils.isValidName
import com.example.core.utils.isValidPassword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(input: CreateUserInput): Flow<DataState<User>> = flow {

        if (input.image.isEmpty()) {
            emit(DataState.Error(ValidationException.InvalidEmptyImageException))
        } else if (input.name.isEmpty()) {
            emit(DataState.Error(ValidationException.InvalidEmptyNameException))
        } else if (input.name.isValidName()) {
            emit(DataState.Error(ValidationException.InvalidNameException))
        } else if (input.bio.isEmpty()) {
            emit(DataState.Error(ValidationException.InvalidEmptyBioException))
        } else if (input.bio.isValidEmail().not()) {
            emit(DataState.Error(ValidationException.InvalidBioException))
        } else if (input.email.isEmpty()) {
            emit(DataState.Error(ValidationException.InvalidEmptyEmailException))
        } else if (input.email.isValidEmail().not()) {
            emit(DataState.Error(ValidationException.InvalidEmailException))
        } else if (input.password.isEmpty()) {
            emit(DataState.Error(ValidationException.InvalidEmptyPasswordException))
        } else if (input.password.isValidPassword().not()) {
            emit(DataState.Error(ValidationException.InvalidPasswordException))
        } else {
            emitAll(repository.createUser(input))
        }

    }

}