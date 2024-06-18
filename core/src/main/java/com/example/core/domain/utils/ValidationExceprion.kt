package com.example.core.domain.utils

sealed class ValidationException : Exception() {

    data object InvalidEmptyNameException : ValidationException()
    data object InvalidNameException : ValidationException()
    data object InvalidEmptyEmailException : ValidationException()
    data object InvalidEmailException : ValidationException()


    data object InvalidEmptyBioException : ValidationException()
    data object InvalidBioException : ValidationException()

    data object InvalidEmptyPasswordException : ValidationException()
    data object InvalidPasswordException : ValidationException()
    data object InvalidEmptyImageException : ValidationException()


}