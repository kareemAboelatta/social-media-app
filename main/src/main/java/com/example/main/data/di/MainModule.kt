package com.example.main.data.di

import com.example.common.AppDispatcher
import com.example.common.Dispatcher
import com.example.main.data.datasource.PostDatasource
import com.example.main.data.datasource.PostDatasourceFirebase
import com.example.main.data.repository.PostsRepositoryImp
import com.example.main.domain.repository.PostsRepository
import com.google.android.datatransport.runtime.dagger.Provides
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Module
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MainModule {


    @Singleton
    @dagger.Provides
    fun providePostDatasource(
        refDatabase: DatabaseReference,
        refStorage: StorageReference,
        @Dispatcher(AppDispatcher.IO)  ioDispatcher: CoroutineDispatcher,
        @Dispatcher(AppDispatcher.Default) defaultDispatcher: CoroutineDispatcher,
    ): PostDatasource = PostDatasourceFirebase(
        refDatabase,
        refStorage,
        ioDispatcher,
        defaultDispatcher
    )


    @Singleton
    @Provides
    fun providerPostsRepository(
        postDatasource: PostDatasource
    ) : PostsRepository = PostsRepositoryImp(postDatasource)







}