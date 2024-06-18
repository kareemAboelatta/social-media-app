package com.domain.usecases

import com.domain.repository.AuthRepository
import com.example.core.domain.utils.ValidationException
import com.example.core.ui.utils.DataState
import com.example.core.utils.isValidEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Flow<DataState<Boolean>> = flow {
        if (email.isEmpty()) {
            emit(DataState.Error(ValidationException.InvalidEmptyEmailException))
        } else if (email.isValidEmail().not()) {
            emit(DataState.Error(ValidationException.InvalidEmailException))
        } else {
            emitAll(repository.resetPassword(email))
        }
    }


}
