package com.aboelatta.universalMediaPreview

import android.content.Context
import androidx.fragment.app.FragmentManager

class PreviewAttachmentDialogBuilder(private val context: Context) {

    private var mediaPreviewAttachments: Array<MediaPreviewAttachment> = arrayOf()
    private var startPosition: Int = 0

    /**
     * Sets the attachments to be previewed.
     *
     * @param mediaPreviewAttachments The array of [MediaPreviewAttachment] objects to be previewed.
     */
    fun setAttachments(mediaPreviewAttachments: List<MediaPreviewAttachment>): PreviewAttachmentDialogBuilder {
        this.mediaPreviewAttachments = mediaPreviewAttachments.toTypedArray()
        return this
    }

    /**
     * Sets the initial position to open in the [ViewPager2].
     *
     * @param position The initial position.
     */
    fun setStartPosition(position: Int): PreviewAttachmentDialogBuilder {
        this.startPosition = position
        return this
    }

    fun show(fragmentManager: FragmentManager) {
        val fragment = PreviewAttachmentDialogFragment.newInstance(mediaPreviewAttachments, startPosition)
        fragment.show(fragmentManager, PreviewAttachmentDialogFragment::class.java.simpleName)
    }
}
