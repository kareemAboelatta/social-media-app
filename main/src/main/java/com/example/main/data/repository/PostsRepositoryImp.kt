package com.example.main.data.repository

import com.example.core.data.remote.safeFirebaseCall
import com.example.core.ui.utils.DataState
import com.example.main.data.datasource.PostDatasource
import com.example.main.domain.model.Post
import com.example.main.domain.model.input.CreatePostInput
import com.example.main.domain.repository.PostsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostsRepositoryImp @Inject constructor(
    private val postDatasource: PostDatasource
) : PostsRepository {

    override suspend fun createPost(input: CreatePostInput): Flow<DataState<Post>> =
        safeFirebaseCall {
            postDatasource.uploadPost(input)
        }

    override suspend fun getAllPosts(): Flow<DataState<List<Post>>> =
        safeFirebaseCall {
            postDatasource.fetchAllPosts()
        }

    override suspend fun fetchVideoPosts(): Flow<DataState<List<Post>>> =
        safeFirebaseCall {
            postDatasource.fetchVideoPosts()
        }

    override suspend fun fetchPostDetails(postId:String): Flow<DataState<Post>> =
        safeFirebaseCall {
            postDatasource.fetchPostDetails(postId)
        }
}