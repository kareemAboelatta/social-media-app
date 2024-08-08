package com.example.common.domain.model


data class Attachment(
    val attachment: String,
    val type: AttachmentType
)

enum class AttachmentType  {
    IMAGE,
    VIDEO
}