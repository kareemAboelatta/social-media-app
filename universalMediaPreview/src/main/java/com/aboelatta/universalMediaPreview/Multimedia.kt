package com.aboelatta.universalMediaPreview

import android.webkit.MimeTypeMap
import java.net.URLConnection
import java.util.*

object Multimedia {

    private val videoExtensions = listOf(
        "mp4", "mkv", "webm", "avi", "mov", "flv", "wmv", "mpg", "mpeg", "3gp", "m4v"
    )

    private val imageExtensions = listOf(
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "svg"
    )

    fun isVideoFile(path: String): Boolean {
        val mimeType = getMimeType(path) ?: return false
        return mimeType.startsWith("video") || videoExtensions.contains(getFileExtension(path).toLowerCase(Locale.ROOT))
    }

    fun isImageFile(path: String): Boolean {
        val mimeType = getMimeType(path) ?: return false
        return mimeType.startsWith("image") || imageExtensions.contains(getFileExtension(path).toLowerCase(Locale.ROOT))
    }

    private fun getMimeType(path: String): String? {
        val extension = getFileExtension(path)
        return if (extension.isNotEmpty()) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.ROOT))
        } else {
            URLConnection.guessContentTypeFromName(path)
        }
    }

    private fun getFileExtension(path: String): String {
        return path.substringAfterLast('.', "")
    }
}
