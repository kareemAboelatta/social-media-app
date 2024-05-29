package com.example.socialmediaapp.main.domain.repository

import com.example.common.ui.utils.Resource
import com.example.common.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostsRepository {
    suspend fun uploadPost(post: Post): Flow<Resource<Nothing>>
}