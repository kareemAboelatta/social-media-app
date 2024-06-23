package com.example.core.ui.pickers

import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.core.R
import com.example.core.ui.ProgressDialogUtil
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.shouheng.compress.Compress
import me.shouheng.compress.concrete
import java.io.File

suspend fun compressFile (context:Context, file: File):String{
        val newResult = Compress.with(context, file)
            .setQuality(95)
            .concrete {
                withMaxWidth(1536f)
                withMaxHeight(1536f)
                withIgnoreIfSmaller(false)
            }.get(Dispatchers.Main)
        return newResult.path
}
suspend fun compressImageFile(context: Context, file: File): String {
    val newResult = Compress.with(context, file)
        .setQuality(95)
        .concrete {
            withMaxWidth(1536f)
            withMaxHeight(1536f)
            withIgnoreIfSmaller(false)
        }.get(Dispatchers.Main)
    return newResult.path
}

// Assuming you have a similar library or method for video compression
suspend fun compressVideoFile(context: Context, file: File): String {
    // Implement your video compression logic here
    // For example, using FFmpeg or any other library
    // Return the path of the compressed video file
    return file.path // Placeholder return statement
}


fun Fragment.pickCompressedImage(progressUtil: ProgressDialogUtil, onSaveFile: suspend (String, Uri) -> (Unit)) {
    TedImagePicker.with(this.requireActivity()).image()
        .start { uri ->
            this.lifecycleScope.launch {
                val file = FileUtils(this@pickCompressedImage.requireContext()).createTmpFileFromUri(uri)
                if (file != null) {
                    progressUtil.showProgress()
                    val compressedFile = compressImageFile(this@pickCompressedImage.requireContext(), file)
                    progressUtil.hideProgress()
                    onSaveFile(compressedFile, uri)
                }
            }
        }
}

fun Fragment.pickCompressedVideo(progressUtil: ProgressDialogUtil, onSaveFile: suspend (String, Uri) -> (Unit)) {
    TedImagePicker.with(this.requireActivity()).video()
        .start { uri ->
            this.lifecycleScope.launch {
                val file = FileUtils(this@pickCompressedVideo.requireContext()).createTmpFileFromUri(uri)
                if (file != null) {
                    progressUtil.showProgress()
                    val compressedFile = compressVideoFile(this@pickCompressedVideo.requireContext(), file)
                    progressUtil.hideProgress()
                    onSaveFile(compressedFile, uri)
                }
            }
        }
}

fun Fragment.pickMultiCompressedImages(listUris:MutableList<Uri>, progressUtil: ProgressDialogUtil
                             , onSaveFiles:suspend (MutableList<String>,List<Uri>)->(Unit)){
    TedImagePicker.with(this.requireContext()).image().max(10,this.requireContext().getString(R.string.max_images)).selectedUri(listUris)
        .startMultiImage{ list ->
            this.lifecycleScope.launch {
                val listImages = mutableListOf<String>()
                list.forEach {
                    val file = FileUtils(this@pickMultiCompressedImages.requireContext()).createTmpFileFromUri(it)
                    if (file != null) {
                        progressUtil.showProgress()
                        val compressedFile = compressFile(this@pickMultiCompressedImages.requireContext(), file)
                        progressUtil.hideProgress()
                        listImages.add(compressedFile)
                    }
                }
                onSaveFiles(listImages,list)
            }
        }
}