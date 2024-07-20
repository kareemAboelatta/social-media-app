package com.example.more.domain.models

import com.example.common.domain.model.User

/** For UI*/
data class UpdateProfileInput(
    val name: String = "",
    val bio: String = "",
    val isImageChanged: Boolean = false,
    val image: String? = null,
)


fun User.mapToUpdateProfileInput(): UpdateProfileInput {
    return UpdateProfileInput(
        name = name.orEmpty(),
        bio = bio.orEmpty(),
        image = image,
    )
}