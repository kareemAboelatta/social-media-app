package com.example.main.domain.model.input

import com.example.common.domain.model.Attachment

data class CreatePostInput(
    val id: String = "",
    val attachments: List<Attachment> = emptyList(),
    val caption: String = "",
    val user: User = User(),
    val postInfo: PostInfo = PostInfo()
)

data class User(
    val id: String? = null,
    val name: String? = null,
    val bio: String? = null,
    val image: String? = null
)

data class PostInfo(
    var createAt: Long = 0,
    var postLikes: Int = 0,
    var postComments: Int = 0,
    var languageCode: String = "und",
)