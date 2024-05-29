package com.example.main.presentation.publish_post

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.common.domain.model.Attachment
import com.example.common.domain.model.AttachmentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class CreatePostInput(
    val caption: String = "",
    /**
     * uri of the image and video to be uploaded*/
    val attachments: MutableList<Attachment> = mutableListOf(),
)



@HiltViewModel
class PublishPostViewModel : ViewModel() {

    private val _inputPost = MutableStateFlow(CreatePostInput())
    val input = _inputPost.asStateFlow()


    fun deleteSelectedAttachment(attachment: Attachment) {
        val attachments = _inputPost.value.attachments.toMutableList().apply {
            remove(attachment)
        }
        updatePostInput(attachments = attachments)
    }
    fun addPhotoAttachment(attachment: String) {
        val newAttachments = _inputPost.value.attachments.toMutableList().apply {
            add( Attachment(attachment, type = AttachmentType.IMAGE) )
        }
        updatePostInput(attachments = newAttachments)
    }
    fun addVideoAttachment(attachment: String, thumbnail: String) {
        val newAttachments = _inputPost.value.attachments.toMutableList().apply {
            add( Attachment(attachment= attachment, thumbnail = thumbnail, type = AttachmentType.VIDEO))
        }
        updatePostInput(attachments = newAttachments)
    }

    fun updatePostInput(
        attachments: MutableList<Attachment>? = null,
        caption: String ? = null
    ) {
        _inputPost.update {
            it.copy(
                attachments = attachments ?: it.attachments,
                caption = caption ?: it.caption
            )
        }
    }

}


