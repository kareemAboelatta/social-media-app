package com.example.main.presentation.publish_post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.common.domain.model.Attachment
import com.example.common.domain.model.AttachmentType
import com.example.core.ui.utils.loadImageFromUrl
import com.example.main.databinding.ItemImageBinding
import com.example.main.databinding.ItemVideoBinding

class AttachmentsAdapter(
    val onAttachmentClicked: (attachment: Attachment) -> Unit,
    val onRemoveAttachment: (attachment: Attachment) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var attachments: List<Attachment> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_IMAGE -> {
                ImageViewHolder(ItemImageBinding.inflate(inflater, parent, false))
            }

            VIEW_TYPE_VIDEO -> {
                VideoViewHolder(ItemVideoBinding.inflate(inflater, parent, false))
            }

            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun getItemCount(): Int {
        return attachments.size // +1 for the add button
    }

    override fun getItemViewType(position: Int): Int {
        return when (attachments[position].type) {
            AttachmentType.IMAGE -> VIEW_TYPE_IMAGE
            AttachmentType.VIDEO -> VIEW_TYPE_VIDEO
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_IMAGE -> (holder as ImageViewHolder).bind(attachments[position])
            VIEW_TYPE_VIDEO -> (holder as VideoViewHolder).bind(attachments[position])
        }
    }

    fun submitAttachmentsList(list: List<Attachment>) {
        this.attachments = list
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attachment: Attachment) {
            binding.image.loadImageFromUrl(attachment.attachment)

            binding.image.setOnClickListener {
                onAttachmentClicked(attachment)
            }
            binding.deleteIcon.setOnClickListener {
                onRemoveAttachment(attachment)
            }
        }
    }

    inner class VideoViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attachment: Attachment) {
            binding.videoThumbnail.loadImageFromUrl(attachment.thumbnail)
            binding.playIcon.visibility = View.VISIBLE

            binding.root.setOnClickListener {
                onAttachmentClicked(attachment)
            }
            binding.deleteIcon.setOnClickListener {
                onRemoveAttachment(attachment)
            }
        }
    }


    companion object {
        private const val VIEW_TYPE_IMAGE = 0
        private const val VIEW_TYPE_VIDEO = 1
    }
}
