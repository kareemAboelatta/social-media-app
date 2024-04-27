package com.example.common.ui.pickers

import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.common.R
import com.example.common.ui.ProgressDialogUtil
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

fun Fragment.pickCompressedImage(progressUtil: ProgressDialogUtil, onSaveFile: suspend (String, Uri) -> (Unit)) {
    TedImagePicker.with(this.requireActivity()).image()
        .start { uri ->
            this.lifecycleScope.launch {
                val file = FileUtils(this@pickCompressedImage.requireContext()).createTmpFileFromUri(uri)
                if (file != null) {
                    progressUtil.showProgressDialog(requireActivity())
                    val compressedFile = compressFile(this@pickCompressedImage.requireContext(), file)
                    progressUtil.hideProgressDialog()
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
                    progressUtil.showProgressDialog(requireActivity())
                    val compressedFile = compressFile(this@pickCompressedVideo.requireContext(), file)
                    progressUtil.hideProgressDialog()
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
                        progressUtil.showProgressDialog(requireActivity())
                        val compressedFile = compressFile(this@pickMultiCompressedImages.requireContext(), file)
                        progressUtil.hideProgressDialog()
                        listImages.add(compressedFile)
                    }
                }
                onSaveFiles(listImages,list)
            }
        }
}