package com.example.common.ui.preview_attachment

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.example.common.databinding.ItemImagePreviewBinding
import com.example.common.databinding.ItemVideoPreviewBinding
import com.example.common.domain.model.Attachment
import com.example.common.domain.model.AttachmentType
import com.example.core.ui.utils.loadImageFromUrl

class PreviewAttachmentsAdapter(
    private val attachments: List<Attachment>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var currentPlayer: ExoPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_IMAGE -> {
                ImageViewHolder(
                    ItemImagePreviewBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_VIDEO -> {
                VideoViewHolder(
                    ItemVideoPreviewBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun getItemCount(): Int = attachments.size

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

    inner class ImageViewHolder(private val binding: ItemImagePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attachment: Attachment) {
            binding.image.loadImageFromUrl(attachment.attachment)
        }
    }

    inner class VideoViewHolder(private val binding: ItemVideoPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Release player when ViewHolder is detached
            binding.root.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    // No action needed
                }

                override fun onViewDetachedFromWindow(v: View) {
                    releasePlayer()
                }
            })
        }

        fun bind(attachment: Attachment) {
            currentPlayer = ExoPlayer.Builder(binding.root.context).build().also {
                binding.videoView.player = it
                val mediaItem = MediaItem.fromUri(Uri.parse(attachment.attachment))
                it.setMediaItem(mediaItem)
                it.prepare()
                it.playWhenReady = true
            }
        }

        private fun releasePlayer() {
            currentPlayer?.let { player ->
                player.stop()
//                player.release()
            }
            currentPlayer = null
        }
    }

    fun stopCurrentPlayer() {
        currentPlayer?.stop()
        currentPlayer = null
    }

    companion object {
        private const val VIEW_TYPE_IMAGE = 0
        private const val VIEW_TYPE_VIDEO = 1
    }
}
