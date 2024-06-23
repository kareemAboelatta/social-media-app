package com.example.common.ui.preview_attachment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.common.databinding.FragmentPreviewAttachmentBinding
import com.example.common.domain.model.Attachment
import com.example.core.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreviewAttachmentFragment :
    BaseFragment<FragmentPreviewAttachmentBinding>(FragmentPreviewAttachmentBinding::inflate) {

    private val args by navArgs<PreviewAttachmentFragmentArgs>()
    private lateinit var adapter: PreviewAttachmentsAdapter

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated() {
        val attachments = args.attachments.toList()
        setupViewPager(attachments)
    }

    private fun setupViewPager(attachments: List<Attachment>) {
        adapter = PreviewAttachmentsAdapter(attachments)
        binding.viewPager.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        adapter.stopCurrentPlayer()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopCurrentPlayer()
    }

    override fun onDestroy() { // sometimes onDestroy is called without onPause or onStop
        super.onDestroy()
        adapter.stopCurrentPlayer()
    }

}
