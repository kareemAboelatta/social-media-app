package com.example.common.domain.model



data class Attachment(
    val attachment: String? = null,
    val type: AttachmentType = AttachmentType.IMAGE,
){
    constructor() : this(null, AttachmentType.IMAGE)
}

enum class AttachmentType  {
    IMAGE,
    VIDEO
}