package com.example.core.domain.utils

sealed class FirebaseExceptions : Exception() {
    data object UnknownException : FirebaseExceptions()
    data object TimeoutException : FirebaseExceptions()
    data object ConnectionException : FirebaseExceptions()
    data object OperationFailedException : FirebaseExceptions()
    data class AuthException(val msg: String) : FirebaseExceptions()

    data class DatabaseException(val msg: String) : FirebaseExceptions()
    data class StorageException(val msg: String) : FirebaseExceptions()


    data class CustomException(val msg: String) : FirebaseExceptions()
}

