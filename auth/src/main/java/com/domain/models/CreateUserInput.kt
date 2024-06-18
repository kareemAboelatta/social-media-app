package com.domain.models

data class CreateUserInput(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val bio: String = "",
    val image: String = "",
)