package com.example.main.domain.usecase

import com.example.common.domain.model.User
import com.example.core.domain.utils.ValidationException
import com.example.core.ui.utils.DataState
import com.example.main.domain.model.input.CreatePostInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(

) {

    operator fun invoke(input: CreatePostInput): Flow<DataState<Unit>> = flow {

        if (input.user.userId == null) {
            emit(DataState.Error(ValidationException.InvalidEmailException))
        }else{

        }

    }

}