package com.example.common.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Attachment(
    val attachment: String,
    val type: AttachmentType
): Parcelable

@Parcelize
enum class AttachmentType : Parcelable {
    IMAGE,
    VIDEO
}