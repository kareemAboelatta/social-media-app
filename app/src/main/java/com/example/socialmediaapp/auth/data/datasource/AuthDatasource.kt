package com.example.socialmediaapp.auth.data.datasource

import android.net.Uri
import com.example.common.ui.utils.Constants
import com.example.socialmediaapp.auth.domain.models.CreateUserInput
import com.example.socialmediaapp.common.AppDispatcher
import com.example.socialmediaapp.common.CustomAuthException
import com.example.socialmediaapp.common.CustomDataException
import com.example.socialmediaapp.common.Dispatcher

import com.example.socialmediaapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AuthDatasource {
    suspend fun createUser(userInput: CreateUserInput): User
    suspend fun signInWithEmailAndPassword(email: String, password: String): User
    suspend fun resetPassword(email: String): Boolean
}

class AuthDatasourceFirebase @Inject constructor(
    private var refDatabase: DatabaseReference,
    private var refStorage: StorageReference,
    private var firebaseMessaging: FirebaseMessaging,
    private var auth: FirebaseAuth,
    @Dispatcher(AppDispatcher.IO) private var ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    @Dispatcher(AppDispatcher.Default) private var defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : AuthDatasource {

    override suspend fun createUser(userInput: CreateUserInput): User =
        withContext(ioDispatcher) {

            try {

                val authResult =
                    auth.createUserWithEmailAndPassword(userInput.email, userInput.password).await()
                val firebaseUser = authResult.user
                val uploadImageResult = async { uploadFile(userInput.image) }.await()

                val newUser = User(
                    id = firebaseUser!!.uid,
                    name = userInput.name,
                    email = userInput.email,
                    bio = userInput.bio,
                    image = uploadImageResult
                )

                setUserInfoOnDatabase(newUser)
            } catch (e: FirebaseAuthException) {
                throw CustomAuthException("Failed to create user: ${e.localizedMessage}")
            } catch (e: Exception) {
                throw CustomDataException("An unexpected error occurred: ${e.localizedMessage}")
            }


        }


    override suspend fun signInWithEmailAndPassword(email: String, password: String): User =
        withContext(ioDispatcher) {

            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                val dataSnapshot =
                    refDatabase.child(Constants.USERS).child(firebaseUser!!.uid).get().await()
                val user: User = dataSnapshot.getValue(User::class.java)!!
                user
            } catch (e: FirebaseAuthException) {
                throw CustomAuthException("Failed to signIn user: ${e.localizedMessage}")
            } catch (e: Exception) {
                throw CustomDataException("An unexpected error occurred: ${e.localizedMessage}")
            }

        }

    override suspend fun resetPassword(email: String): Boolean = withContext(ioDispatcher) {
        try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: FirebaseAuthException) {
            throw CustomAuthException("Failed to signIn user: ${e.localizedMessage}")
        } catch (e: Exception) {
            throw CustomDataException("An unexpected error occurred: ${e.localizedMessage}")
        }
    }


    private suspend fun uploadFile(uri: Uri): String = withContext(defaultDispatcher) {
        val uploadTask = refStorage.child(Constants.IMAGES).putFile(uri).await()
        uploadTask.storage.downloadUrl.await().toString()
    }

    private suspend fun setUserInfoOnDatabase(user: User): User = coroutineScope {
        val token = firebaseMessaging.token.await()
        user.token = token
        refDatabase.child(Constants.USERS).child(user.id).setValue(user).await()
        user
    }


}