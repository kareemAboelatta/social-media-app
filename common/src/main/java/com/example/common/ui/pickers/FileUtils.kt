package com.example.common.ui.pickers

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class FileUtils constructor(val context: Context) {

    fun saveFileInStorage(uri: Uri): String {
        var path = ""
        runBlocking {
            try {
                val file: File?
                val mimeType: String? = context.contentResolver.getType(uri)
                if (mimeType != null) {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val fileName = context.contentResolver.getFileName(uri)

                    if (fileName != "") {
                        file = File(
                            context.getExternalFilesDir(
                                Environment.DIRECTORY_DOWNLOADS
                            )?.absolutePath.toString() + "/" + fileName
                        )
                        val output: OutputStream = FileOutputStream(file)
                        output.use { it ->
                            val buffer =
                                ByteArray(inputStream?.available()!!)
                            var read: Int
                            while (inputStream.read(buffer).also { read = it } != -1) {
                                it.write(buffer, 0, read)
                            }
                            it.flush()
                            path = file.absolutePath //use this path
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return path
    }

    fun saveFileStorage(uri: Uri): File? {
        var path:File? = null
        runBlocking {
            try {
                val file: File?
                val mimeType: String? = context.contentResolver.getType(uri)
                if (mimeType != null) {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val fileName = context.contentResolver.getFileName(uri)

                    if (fileName != "") {
                        file = File(
                            context.getExternalFilesDir(
                                Environment.DIRECTORY_DOWNLOADS
                            )?.absolutePath.toString() + "/" + fileName
                        )
                        val output: OutputStream = FileOutputStream(file)
                        output.use { it ->
                            val buffer =
                                ByteArray(inputStream?.available()!!)
                            var read: Int
                            while (inputStream.read(buffer).also { read = it } != -1) {
                                it.write(buffer, 0, read)
                            }
                            it.flush()
                            path = file //use this path
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return path
    }

    @SuppressLint("Range")
    fun ContentResolver.getFileName(uri: Uri): String {
        var mName = ""
        val cursor = query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            mName = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
        return mName
    }

    fun createTmpFileFromUri(uri: Uri): File? {
        return try {
            val stream = context.contentResolver.openInputStream(uri)
            val file = File.createTempFile( "where"+context.contentResolver.getFileName(uri), "", context.cacheDir)
            org.apache.commons.io.FileUtils.copyInputStreamToFile(stream,file)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareApp(mContext: Context) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "https://play.google.com/store/apps/details?id=" + mContext.packageName
        )
        sendIntent.type = "text/plain"
        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext.startActivity(sendIntent)
    }
}
