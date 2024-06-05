package com.example.common.data.datasource

import com.example.common.AppDispatcher
import com.example.common.CustomDataException
import com.example.common.Dispatcher
import com.example.common.UnauthorizedException
import com.example.common.domain.model.User
import com.example.common.ui.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface UserDatasource {
    suspend fun getUserInfo(userId: String): User
    suspend fun getCurrentUserInfo(): User
}

class UserDatasourceFirebase @Inject constructor(
    private var refDatabase: DatabaseReference,
    private var auth: FirebaseAuth,
    @Dispatcher(AppDispatcher.IO) private var ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    @Dispatcher(AppDispatcher.Default) private var defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : UserDatasource {
    override suspend fun getUserInfo(userId: String): User = withContext(ioDispatcher) {
        try {
            val dataSnapshot = refDatabase.child(Constants.USERS).child(userId).get().await()
            val user: User =
                dataSnapshot?.getValue(User::class.java) ?: throw UnauthorizedException()
            user
        } catch (e: Exception) {
            throw CustomDataException("An unexpected error occurred: ${e.localizedMessage}")
        }

    }

    override suspend fun getCurrentUserInfo(): User = withContext(ioDispatcher) {
        try {
            val dataSnapshot = auth.currentUser?.uid?.let {
                refDatabase.child(Constants.USERS).child(it).get().await()
            }
            val user: User =
                dataSnapshot?.getValue(User::class.java) ?: throw UnauthorizedException()
            user
        } catch (e: Exception) {
            throw CustomDataException("An unexpected error occurred: ${e.localizedMessage}")
        }

    }

}