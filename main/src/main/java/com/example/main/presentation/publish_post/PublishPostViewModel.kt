package com.example.main.presentation.publish_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.data.local.UserPreferences
import com.example.common.domain.model.Attachment
import com.example.common.domain.model.AttachmentType
import com.example.main.domain.model.input.CreatePostInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class PublishPostViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _inputPost = MutableStateFlow(CreatePostInput())
    val input = _inputPost.asStateFlow()


    val user = userPreferences.user
        .stateIn(viewModelScope, SharingStarted.Lazily, null)


    fun deleteSelectedAttachment(attachment: Attachment) {
        val attachments = _inputPost.value.attachments.toMutableList().apply {
            remove(attachment)
        }
        updatePostInput(attachments = attachments)
    }

    fun addPhotoAttachment(attachment: String) {
        val newAttachments = _inputPost.value.attachments.toMutableList().apply {
            add(Attachment(attachment, type = AttachmentType.IMAGE))
        }
        updatePostInput(attachments = newAttachments)
    }

    fun addVideoAttachment(attachment: String) {
        val newAttachments = _inputPost.value.attachments.toMutableList().apply {
            add(Attachment(attachment = attachment, type = AttachmentType.VIDEO))
        }
        updatePostInput(attachments = newAttachments)
    }

    fun updatePostInput(
        userId: String? = null,
        name: String? = null,
        bio: String? = null,
        image: String? = null,
        attachments: MutableList<Attachment>? = null,
        caption: String? = null
    ) {
        _inputPost.update {
            it.copy(
                attachments = attachments ?: it.attachments,
                caption = caption ?: it.caption,
                user = it.user.copy(
                    userId = userId ?: it.user.userId,
                    name = name ?: it.user.name,
                    bio = bio ?: it.user.bio,
                    image = image ?: it.user.image
                ),
            )
        }
    }

}


