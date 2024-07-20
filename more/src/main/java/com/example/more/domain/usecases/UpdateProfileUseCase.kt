package com.example.more.domain.usecases

import com.example.common.domain.model.User
import com.example.core.ui.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor() {

    suspend operator fun invoke(input: User): Flow<DataState<User>> = flow {
        emit(DataState.Loading)
        emit(DataState.Success(input))
    }
}