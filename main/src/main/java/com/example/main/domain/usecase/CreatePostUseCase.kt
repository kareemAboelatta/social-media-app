package com.example.main.domain.usecase

import com.example.core.domain.utils.ValidationException
import com.example.core.ui.utils.DataState
import com.example.main.domain.model.Post
import com.example.main.domain.model.input.CreatePostInput
import com.example.main.domain.repository.PostsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val postsRepository: PostsRepository
) {

    suspend operator fun invoke(input: CreatePostInput): Flow<DataState<Post>> = flow {
        if (input.user.userId == null) {
            emit(DataState.Error(ValidationException.InvalidEmailException))
        } else  if (input.attachments.isEmpty() or input.caption.isEmpty()) {
            emit(DataState.Error(ValidationException.InvalidEmptyContentException))
        } else  {
            emitAll(postsRepository.createPost(input))
        }

    }

}