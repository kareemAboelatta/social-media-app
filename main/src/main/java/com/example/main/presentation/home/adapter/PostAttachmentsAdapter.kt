package com.example.main.presentation.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.common.domain.model.Attachment
import com.example.common.domain.model.AttachmentType
import com.example.core.ui.utils.loadImageFromUrl
import com.example.main.databinding.ItemImageBinding
import com.example.main.databinding.ItemVideoBinding
import kotlin.reflect.KProperty0

class PostAttachmentsAdapter(
    val onAttachmentClicked: (attachments: List<Attachment>, position: Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val differCallback = object : DiffUtil.ItemCallback<Attachment>() {
        override fun areItemsTheSame(oldItem: Attachment, newItem: Attachment): Boolean {
            return oldItem.attachment == newItem.attachment
        }

        override fun areContentsTheSame(oldItem: Attachment, newItem: Attachment): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<Attachment>) {
        differ.submitList(list)
    }


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
        return differ.currentList.size // +1 for the add button
    }

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position].type) {
            AttachmentType.IMAGE -> VIEW_TYPE_IMAGE
            AttachmentType.VIDEO -> VIEW_TYPE_VIDEO
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_IMAGE -> (holder as ImageViewHolder).bind(differ.currentList[position])
            VIEW_TYPE_VIDEO -> (holder as VideoViewHolder).bind(differ.currentList[position])
        }
    }


    inner class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attachment: Attachment) {
            binding.image.loadImageFromUrl(attachment.attachment)

            binding.image.setOnClickListener {
                onAttachmentClicked(differ.currentList,absoluteAdapterPosition)
            }

        }
    }

    inner class VideoViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attachment: Attachment) {
            binding.videoThumbnail.loadImageFromUrl(attachment.attachment)
            binding.playIcon.visibility = View.VISIBLE

            binding.root.setOnClickListener {
                onAttachmentClicked(differ.currentList,absoluteAdapterPosition)
            }

        }
    }


    companion object {
        private const val VIEW_TYPE_IMAGE = 0
        private const val VIEW_TYPE_VIDEO = 1
    }
}
