package com.di


import com.data.datasource.AuthDatasource
import com.data.datasource.AuthDatasourceFirebase
import com.data.repository.AuthRepositoryImpl
import com.domain.repository.AuthRepository
import com.example.common.AppDispatcher
import com.example.common.Dispatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideAuthRepository(
        authDatasource: AuthDatasource
    ): AuthRepository = AuthRepositoryImpl(
        authDatasource = authDatasource
    )



    @Singleton
    @Provides
    fun provideAuthDatasource(
        refDatabase: DatabaseReference,
        refStorage: StorageReference,
        firebaseMessaging: FirebaseMessaging,
        auth: FirebaseAuth,
        @Dispatcher(AppDispatcher.IO)  ioDispatcher: CoroutineDispatcher,
        @Dispatcher(AppDispatcher.Default) defaultDispatcher: CoroutineDispatcher,
    ): AuthDatasource = AuthDatasourceFirebase(
        refDatabase,
        refStorage,
        firebaseMessaging,
        auth,
        ioDispatcher,
        defaultDispatcher
    )


}