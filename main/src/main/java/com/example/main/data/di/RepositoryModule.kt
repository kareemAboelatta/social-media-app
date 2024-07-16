package com.example.main.data.di

import com.example.main.data.repository.PostsRepositoryImp
import com.example.main.domain.repository.PostsRepository
import com.google.android.datatransport.runtime.dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Module
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MainModule {


    @Singleton
    @Provides
    fun providerMyName() : PostsRepository = PostsRepositoryImp()


}