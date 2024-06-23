package com.example.common.ui.preview_attachment

import android.net.Uri
import android.util.Log
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


/**
 * RecyclerView.Adapter implementation to display a list of attachments (images and videos).
 * Handles the binding of data to view holders and manages ExoPlayer instances for video playback.
 *
 * @property attachments List of attachments to be displayed.
 */

class PreviewAttachmentsAdapter(
    private val attachments: List<Attachment>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // ExoPlayer instance for the current playing video
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

    /**
     * Called when a view created by this adapter has been detached from its window.
     */
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)

        if (holder is VideoViewHolder) {
            holder.releasePlayer()
        }

    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is VideoViewHolder) {
            holder.playVideo()
        }
    }


    inner class VideoViewHolder(private val binding: ItemVideoPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

            var currentPlayerForHolder: ExoPlayer? = null

        fun bind(attachment: Attachment) {
            currentPlayer = ExoPlayer.Builder(binding.root.context).build().also {
                val mediaItem = MediaItem.fromUri(Uri.parse(attachment.attachment))
                it.setMediaItem(mediaItem)
                it.prepare()
                binding.videoView.player = it
            }
            currentPlayerForHolder = currentPlayer

        }
        /**
         * Releases the ExoPlayer instance when the item is not visible because of scrolling.
         * because exo player work on in background thread so it will play even the video is not visible
         */
        fun releasePlayer() {
            currentPlayerForHolder?.playWhenReady = false
            currentPlayerForHolder?.pause()
        }

        fun playVideo() {

            currentPlayerForHolder?.playWhenReady = true
            currentPlayerForHolder?.play()

        }
    }

    /**
     * Stops and releases the currently playing ExoPlayer instance, if any.
     */
    fun stopCurrentPlayer() {
        currentPlayer?.playWhenReady = false
        currentPlayer?.pause()
    }

    companion object {
        private const val VIEW_TYPE_IMAGE = 0
        private const val VIEW_TYPE_VIDEO = 1
    }
}

