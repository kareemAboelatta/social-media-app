package com.example.socialmediaapp.di


import android.content.Context
import com.example.socialmediaapp.repository.Repository
import com.example.socialmediaapp.repository.RepositoryMessenger
import com.example.socialmediaapp.repository.RepositoryUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        refDatabase: DatabaseReference,
        refStorage: StorageReference,
        auth: FirebaseAuth,
        firebaseMessaging: FirebaseMessaging,
        @ApplicationContext
        context: Context
    )= Repository(refDatabase,refStorage,auth,firebaseMessaging,context)


    @Singleton
    @Provides
    fun provideMessengerRepository(
        refDatabase: DatabaseReference,
        auth: FirebaseAuth,
        @ApplicationContext
        context: Context
    )= RepositoryMessenger(refDatabase,auth,context)



    @Singleton
    @Provides
    fun provideUserRepository(
        refDatabase: DatabaseReference,
        refStorage: StorageReference,
        firebaseMessaging: FirebaseMessaging,
        auth: FirebaseAuth,
        @ApplicationContext
        context: Context
    )= RepositoryUser(refDatabase,refStorage,firebaseMessaging,auth,context)

}