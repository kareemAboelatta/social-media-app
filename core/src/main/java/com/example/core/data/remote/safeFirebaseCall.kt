package com.example.core.data.remote

import android.util.Log
import com.example.core.HANDLE_STATES_TAG
import com.example.core.data.NetworkConstants.NETWORK_TIMEOUT
import com.example.core.domain.utils.FirebaseExceptions
import com.example.core.domain.utils.getFirebaseAuthErrorMessage
import com.example.core.domain.utils.getFirebaseDatabaseErrorMessage
import com.example.core.domain.utils.getFirebaseStorageErrorMessage
import com.example.core.ui.utils.DataState
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseException
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeout
import java.io.IOException

suspend fun <T> safeFirebaseCall(
    firebaseCall: suspend () -> T
): Flow<DataState<T>> = flow {
    withTimeout(NETWORK_TIMEOUT) {
        val response = firebaseCall.invoke()
        emit(handleFirebaseSuccess(response))
    }
}.onStart {
    emit(DataState.Loading)
}.catch { exception ->
    emit(handleFirebaseError(exception))
}.flowOn(Dispatchers.IO)

fun <T> handleFirebaseSuccess(response: T): DataState<T> {
    return if (response != null) DataState.Success(response)
    else DataState.Error(FirebaseExceptions.UnknownException)
}

fun <T> handleFirebaseError(throwable: Throwable): DataState<T> {
    throwable.printStackTrace()

    return when (throwable) {
        is TimeoutCancellationException -> DataState.Error(FirebaseExceptions.TimeoutException)
        is IOException -> DataState.Error(FirebaseExceptions.ConnectionException)
        is FirebaseAuthException -> {
            val errorMessage = getFirebaseAuthErrorMessage(throwable.errorCode)
            Log.d(HANDLE_STATES_TAG, "handleFirebaseError: throwable.errorCode::: {${throwable.errorCode}}")
            DataState.Error(FirebaseExceptions.AuthException(errorMessage))
        }
        is DatabaseException -> {
            val errorMessage = getFirebaseDatabaseErrorMessage(throwable.message)
            DataState.Error(FirebaseExceptions.DatabaseException(errorMessage))
        }
        is StorageException -> {
            val errorMessage = getFirebaseStorageErrorMessage(throwable.message)
            DataState.Error(FirebaseExceptions.StorageException(errorMessage))
        }
        else -> DataState.Error(FirebaseExceptions.UnknownException)
    }
}
