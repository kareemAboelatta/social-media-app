package com.example.main.presentation.publish_post

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.common.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


data class CreatePostInput(
    val caption: String = "",
    /**
     * uris of the image and video to be uploaded*/
    val attachments: List<String> = emptyList(),
    val user : User? = null
)
@HiltViewModel
class PublishPostViewModel : ViewModel() {

    private val _input = MutableStateFlow<CreatePostInput>(CreatePostInput())
    val input = _input.asStateFlow()








}

object VideoThumbnailUtil {

    fun getVideoThumbnail(context: Context, videoUri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        val bitmap = retriever.getFrameAtTime(0)
        retriever.release()
        return bitmap
    }
}

