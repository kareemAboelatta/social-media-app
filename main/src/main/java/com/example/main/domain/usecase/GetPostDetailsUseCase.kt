package com.example.main.domain.usecase

import com.example.core.ui.utils.DataState
import com.example.main.domain.model.Post
import com.example.main.domain.repository.PostsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPostDetailsUseCase @Inject constructor(
    private val postsRepository: PostsRepository
) {

    suspend operator fun invoke(postId: String): Flow<DataState<Post>> = flow {
        emitAll(postsRepository.fetchPostDetails(postId))
    }

}