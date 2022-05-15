package com.example.socialmediaapp.di


import com.example.socialmediaapp.network.NotificationApi
import com.example.socialmediaapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn

import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideDatabaseReference() = FirebaseDatabase.getInstance().reference

    @Singleton
    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()


    @Singleton
    @Provides
    fun provideFirebaseStorage() = FirebaseStorage.getInstance().reference


    @Singleton
    @Provides
    fun provideFirebaseMessaging() = FirebaseMessaging.getInstance()


    @Singleton
    @Provides
    fun provideMovieService(): NotificationApi{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build()
            .create(NotificationApi::class.java)
    }


}