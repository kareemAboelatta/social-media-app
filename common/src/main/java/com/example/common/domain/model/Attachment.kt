package com.example.common.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class Attachment(
    val attachment: String,
    val type: AttachmentType
)

enum class AttachmentType  {
    IMAGE,
    VIDEO
}