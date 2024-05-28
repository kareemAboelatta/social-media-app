package com.example.main.presentation.publish_post

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun Context.getVideoThumbnailUri(videoUri: Uri): Uri? {
    // Step 1: Extract thumbnail from video
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(this, videoUri)
    val thumbnailBitmap: Bitmap? = retriever.frameAtTime

    // Step 2: Save thumbnail to device temporarily
    thumbnailBitmap?.let {
        val filename = "thumbnail_${System.currentTimeMillis()}.jpg"
        val directory = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(directory, filename)
        try {
            val fos = FileOutputStream(file)
            it.compress(Bitmap.CompressFormat.JPEG, 80, fos)
            fos.flush()
            fos.close()
            return Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return null
}
