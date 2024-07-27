package com.example.main.domain.repository

import com.example.core.ui.utils.DataState
import com.example.main.domain.model.Post
import com.example.main.domain.model.input.CreatePostInput
import kotlinx.coroutines.flow.Flow

interface PostsRepository {
    suspend fun createPost(input: CreatePostInput): Flow<DataState<Post>>
    suspend fun getAllPosts(): Flow<DataState<List<Post>>>
    suspend fun fetchVideoPosts(): Flow<DataState<List<Post>>>

}