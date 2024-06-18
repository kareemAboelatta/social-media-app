package com.domain.usecases

import com.domain.repository.AuthRepository
import com.example.common.domain.model.User
import com.example.core.domain.utils.ValidationException
import com.example.core.ui.utils.DataState
import com.example.core.utils.isValidEmail
import com.example.core.utils.isValidPassword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Flow<DataState<User>> = flow {

        if (email.isEmpty()) {
            emit(DataState.Error(ValidationException.InvalidEmptyEmailException))
        } else if (email.isValidEmail().not()) {
            emit(DataState.Error(ValidationException.InvalidEmailException))
        } else if (password.isEmpty()) {
            emit(DataState.Error(ValidationException.InvalidEmptyPasswordException))
        } else if (password.isValidPassword().not()) {
            emit(DataState.Error(ValidationException.InvalidPasswordException))
        } else {
            emitAll(repository.signInWithEmailAndPassword(email, password))
        }
    }


}