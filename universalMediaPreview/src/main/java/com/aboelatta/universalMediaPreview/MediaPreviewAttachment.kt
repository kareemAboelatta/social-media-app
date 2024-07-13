package com.aboelatta.universalMediaPreview

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaPreviewAttachment(
    val attachment: String,
    val type: MediaPreviewType
) : Parcelable


@Parcelize
enum class MediaPreviewType :Parcelable {
    IMAGE,
    VIDEO
}
