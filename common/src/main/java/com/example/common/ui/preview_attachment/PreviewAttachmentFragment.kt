package com.example.common.ui.preview_attachment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.navArgs
import com.example.common.databinding.FragmentPreviewAttachmentBinding
import com.example.common.domain.model.Attachment
import com.example.core.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreviewAttachmentFragment :
    BaseFragment<FragmentPreviewAttachmentBinding>(FragmentPreviewAttachmentBinding::inflate) {

        private val args by navArgs<PreviewAttachmentFragmentArgs>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated() {
        val attachments =
            args.attachments.toList()

        attachments.let { setupViewPager(it) }

    }

    private fun setupViewPager(attachments: List<Attachment>) {
        val adapter = PreviewAttachmentsAdapter(attachments)
        binding.viewPager.adapter = adapter

    }
}
