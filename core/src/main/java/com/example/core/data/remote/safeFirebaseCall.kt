package com.example.core.data.remote

import com.example.core.data.NetworkConstants.NETWORK_TIMEOUT
import com.example.core.domain.utils.FirebaseExceptions
import com.example.core.ui.utils.DataState
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseException
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
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
}.catch {
    emit(handleFirebaseError(it))
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
        is FirebaseAuthException -> DataState.Error(
            FirebaseExceptions.AuthException(
                throwable.localizedMessage ?: "Firebase Auth Error"
            )
        )

        is DatabaseException -> DataState.Error(
            FirebaseExceptions.DatabaseException(
                throwable.localizedMessage ?: "Firebase Database Error"
            )
        )

        is StorageException -> DataState.Error(
            FirebaseExceptions.StorageException(
                throwable.localizedMessage ?: "Firebase Storage Error"
            )
        )

        else -> DataState.Error(FirebaseExceptions.UnknownException)
    }
}
