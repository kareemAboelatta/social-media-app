package com.aboelatta.universalMediaPreview

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.*
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.CompositePageTransformer
import com.aboelatta.universalMediaPreview.databinding.FragmentPreviewAttachmentDialogBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class PreviewAttachmentDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentPreviewAttachmentDialogBinding

    private lateinit var previewAttachmentsAdapter: PreviewAttachmentsAdapter
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.FullScreenDialogStyle).apply {
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            window?.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
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

        gestureDetector = GestureDetector(requireContext(), GestureListener(
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
