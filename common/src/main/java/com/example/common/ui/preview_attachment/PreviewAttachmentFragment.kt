package com.example.common.ui.preview_attachment

import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import androidx.annotation.RequiresApi
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.navArgs
import com.example.common.R
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val transition: Transition = TransitionInflater.from(context)
            .inflateTransition(R.transition.shared_attachment_element_transition)
        transition.duration = 2000 // Set duration to 1000 milliseconds (1 second)

        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
    }
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated() {

        val openedPosition: Int = args.clickedPosition
        val attachments = args.attachments.toList()
        setupViewPager(attachments = attachments, position = openedPosition)
        postponeEnterTransition()
        binding.viewPager.doOnPreDraw { startPostponedEnterTransition() }
    }


    private fun setupViewPager(attachments: List<Attachment>, position: Int = 0) {
        adapter = PreviewAttachmentsAdapter(attachments)
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(position, false)
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
