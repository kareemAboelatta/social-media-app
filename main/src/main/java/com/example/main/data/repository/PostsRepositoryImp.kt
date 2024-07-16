package com.example.main.data.repository

import com.example.core.ui.utils.DataState
import com.example.main.domain.model.input.CreatePostInput
import com.example.main.domain.repository.PostsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostsRepositoryImp @Inject constructor() : PostsRepository {

    override suspend fun createPost(input: CreatePostInput): Flow<DataState<Unit>> {
        TODO("Not yet implemented")
    }
}