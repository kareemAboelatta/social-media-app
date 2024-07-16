package com.example.main.domain.model

import com.example.common.domain.model.Attachment
import com.example.main.domain.model.input.PostInfo
import com.example.main.domain.model.input.User

data class Post(
    val id: String = "",
    val attachments: List<Attachment> = emptyList(),
    val caption: String = "",
    val user: User = User(),
    val postInfo: PostInfo = PostInfo()
)
