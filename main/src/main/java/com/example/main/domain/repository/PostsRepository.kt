package com.example.main.domain.repository

import com.example.core.ui.utils.DataState
import com.example.main.domain.model.input.CreatePostInput
import kotlinx.coroutines.flow.Flow

interface PostsRepository {

    suspend fun createPost(input: CreatePostInput): Flow<DataState<Unit>>
}