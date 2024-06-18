package com.example.core.domain.utils

fun getFirebaseAuthErrorMessage(errorCode: String): String {
    return when (errorCode) {
        "ERROR_INVALID_CUSTOM_TOKEN" -> "The custom token format is incorrect or the token is invalid."
        "ERROR_CUSTOM_TOKEN_MISMATCH" -> "The custom token corresponds to a different Firebase project."
        "ERROR_INVALID_CREDENTIAL" -> "The supplied authentication credential is malformed or has expired."
        "ERROR_INVALID_EMAIL" -> "The email address is badly formatted."
        "ERROR_WRONG_PASSWORD" -> "The password is incorrect. Please try again."
        "ERROR_USER_MISMATCH" -> "The supplied credentials do not match the previously signed-in user."
        "ERROR_REQUIRES_RECENT_LOGIN" -> "This operation is sensitive and requires recent authentication. Please log in again."
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "An account already exists with the same email address but different sign-in credentials. Use a different sign-in method."
        "ERROR_EMAIL_ALREADY_IN_USE" -> "The email address is already in use by another account."
        "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "This credential is already associated with a different user account."
        "ERROR_USER_DISABLED" -> "The user account has been disabled by an administrator."
        "ERROR_USER_TOKEN_EXPIRED" -> "The user's credential is no longer valid. Please log in again."
        "ERROR_USER_NOT_FOUND" -> "There is no user record corresponding to this identifier. The user may have been deleted."
        "ERROR_INVALID_USER_TOKEN" -> "The user's credential is no longer valid. Please log in again."
        "ERROR_OPERATION_NOT_ALLOWED" -> "This operation is not allowed. Please enable this service in the Firebase console."
        "ERROR_WEAK_PASSWORD" -> "The password provided is too weak. Please use a stronger password."
        "ERROR_TOO_MANY_REQUESTS" -> "We have blocked all requests from this device due to unusual activity. Please try again later."
        "ERROR_PROVIDER_ALREADY_LINKED" -> "This provider is already linked to a user account."
        "ERROR_NO_SUCH_PROVIDER" -> "User was not linked to an account with the given provider."
        else -> "An unknown error occurred. Please try again."
    }
}

fun getFirebaseDatabaseErrorMessage(errorCode: String?): String {
    return when (errorCode) {
        "DATABASE_DISCONNECTED" -> "The database connection was lost."
        "DATABASE_PERMISSION_DENIED" -> "You do not have permission to access the database."
        "DATABASE_NETWORK_ERROR" -> "A network error occurred while trying to access the database."
        "DATABASE_WRITE_CANCELED" -> "The write operation was canceled."
        else -> "A database error occurred. Please try again."
    }
}

fun getFirebaseStorageErrorMessage(errorCode: String?): String {
    return when (errorCode) {
        "STORAGE_UNKNOWN" -> "An unknown error occurred."
        "STORAGE_OBJECT_NOT_FOUND" -> "No object exists at the desired reference."
        "STORAGE_BUCKET_NOT_FOUND" -> "No bucket is configured for Cloud Storage."
        "STORAGE_PROJECT_NOT_FOUND" -> "No project is configured for Cloud Storage."
        "STORAGE_QUOTA_EXCEEDED" -> "Quota on your Cloud Storage bucket has been exceeded."
        "STORAGE_UNAUTHENTICATED" -> "User is unauthenticated. Authenticate and try again."
        "STORAGE_UNAUTHORIZED" -> "User is not authorized to perform the desired action."
        "STORAGE_RETRY_LIMIT_EXCEEDED" -> "The operation retry limit was exceeded."
        "STORAGE_INVALID_CHECKSUM" -> "The file on the client does not match the checksum of the file received by the server."
        "STORAGE_CANCELED" -> "The operation was canceled."
        "STORAGE_INVALID_ARGUMENT" -> "An invalid argument was provided."
        else -> "A storage error occurred. Please try again."
    }
}


