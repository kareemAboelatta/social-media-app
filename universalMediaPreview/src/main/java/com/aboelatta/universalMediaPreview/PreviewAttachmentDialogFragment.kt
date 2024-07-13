package com.aboelatta.universalMediaPreview

import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.CompositePageTransformer
import com.aboelatta.universalMediaPreview.databinding.FragmentPreviewAttachmentDialogBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class PreviewAttachmentDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentPreviewAttachmentDialogBinding
    private var visible: Boolean = false

    private lateinit var previewAttachmentsAdapter: PreviewAttachmentsAdapter
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)

        val mediaPreviewAttachments: List<MediaPreviewAttachment> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelableArray(ATTACHMENTS, MediaPreviewAttachment::class.java)
                    ?.mapNotNull { it } ?: emptyList()
            } else {
                arguments?.getParcelableArray(ATTACHMENTS)?.mapNotNull {
                    it as? MediaPreviewAttachment
                } ?: emptyList()
            }

        previewAttachmentsAdapter = PreviewAttachmentsAdapter(attachments = mediaPreviewAttachments)

        val transition: Transition = TransitionInflater.from(context)
            .inflateTransition(R.transition.shared_attachment_element_transition)
        transition.duration = 2000 // Set duration to 1000 milliseconds (1 second)

        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreviewAttachmentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setupSlider(position: Int = 0) {
        with(binding.viewPager) {
            setPageTransformer(CompositePageTransformer())
            adapter = previewAttachmentsAdapter
            binding.viewPager.setCurrentItem(position, false)
            postponeEnterTransition()
            binding.viewPager.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSlider(position = arguments?.getInt(OPENED_POSITION) ?: 0)
        visible = true

        binding.container.setOnClickListener { toggle() }

        gestureDetector = GestureDetector(requireContext(), GestureListener(
            onTouch = { delayedHide() },
            onSwipeDownTouch = { dismiss() }
        ))
        binding.container.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            false
        }

        binding.btnBack.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        // Set the dialog to full-screen
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        lifecycleScope.launch {
            delay(100)
            hide()
        }
    }

    private fun toggle() {
        if (visible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        binding.fullscreenContentControls.visibility = View.INVISIBLE
        visible = false
        lifecycleScope.launch {
            delay(UI_ANIMATION_DELAY.toLong())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity?.window?.insetsController?.hide(
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                )
            }
        }
    }

    private fun show() {
        visible = true

        lifecycleScope.launch {
            delay(UI_ANIMATION_DELAY.toLong())
            binding.fullscreenContentControls.visibility = View.VISIBLE
            delay(AUTO_HIDE_DELAY_MILLIS)
            hide()
        }
    }

    private fun delayedHide() {
        lifecycleScope.launch {
            delay(AUTO_HIDE_DELAY_MILLIS)
            hide()
        }
    }

    // close video player  when fragment is destroyed because sometimes onDestroy is called without onStop
    override fun onDestroy() {
        super.onDestroy()
        previewAttachmentsAdapter.stopCurrentPlayer()
    }

    override fun onPause() {
        super.onPause()
        previewAttachmentsAdapter.stopCurrentPlayer()
    }

    override fun onStop() {
        super.onStop()
        previewAttachmentsAdapter.stopCurrentPlayer()
    }

    companion object {
        private const val AUTO_HIDE_DELAY_MILLIS = 3000L
        private const val UI_ANIMATION_DELAY = 300
        private const val ATTACHMENTS = "attachments"
        private const val OPENED_POSITION = "openedPosition"

        fun newInstance(
            mediaPreviewAttachments: Array<MediaPreviewAttachment>,
            openedPosition: Int
        ): PreviewAttachmentDialogFragment {
            return PreviewAttachmentDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArray(ATTACHMENTS, mediaPreviewAttachments)
                    putInt(OPENED_POSITION, openedPosition)
                }
            }
        }
    }
}
