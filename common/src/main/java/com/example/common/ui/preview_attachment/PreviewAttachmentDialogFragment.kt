package com.example.common.ui.preview_attachment

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.common.R
import com.example.common.databinding.FragmentPreviewAttachmentBinding
import com.example.common.domain.model.Attachment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreviewAttachmentDialogFragment : DialogFragment() {

    private val args by navArgs<PreviewAttachmentDialogFragmentArgs>()
    private lateinit var binding: FragmentPreviewAttachmentBinding
    private lateinit var adapter: PreviewAttachmentsAdapter

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val transition = TransitionInflater.from(context).inflateTransition(R.transition.shared_attachment_element_transition)
        transition.duration = 1000 // Set duration to 1000 milliseconds (1 second)

        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition

        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreviewAttachmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopCurrentPlayer()
    }
}
