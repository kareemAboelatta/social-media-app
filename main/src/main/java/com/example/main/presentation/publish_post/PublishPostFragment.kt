package com.example.main.presentation.publish_post

import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.core.BaseFragment
import com.example.core.ui.ProgressDialogUtil
import com.example.core.ui.pickers.pickCompressedImage
import com.example.core.ui.pickers.pickCompressedVideo
import com.example.main.databinding.FragmentPublishPostBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.common.R as commonR


@AndroidEntryPoint
class PublishPostFragment :
    BaseFragment<FragmentPublishPostBinding>(FragmentPublishPostBinding::inflate) {


    private var isFabOpen = false


    private val viewModel: PublishPostViewModel by viewModels()
    private lateinit var attachmentAdapter: AttachmentsAdapter
    override fun onViewCreated() {
        attachmentAdapter = AttachmentsAdapter(
            onAttachmentClicked = {
                findNavController().navigate(
                    PublishPostFragmentDirections.actionToPreviewAttachmentFragment(viewModel.input.value.attachments.toTypedArray())
                )
            },
            onRemoveAttachment = {
                viewModel.deleteSelectedAttachment(it)
            }
        )
        binding.rvAttachments.adapter = attachmentAdapter

        observeAttachments()

    }


    private fun observeAttachments() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.input.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect {
                attachmentAdapter.submitAttachmentsList(it.attachments)
            }
        }
    }

    override fun onClicks() {
        with(binding) {
            fabAttachmentMenu.setOnClickListener {
                if (isFabOpen) {
                    closeFabMenu()
                } else {
                    openFabMenu()
                }
            }


            fabAddVideo.setOnClickListener {
                pickCompressedVideo(progressUtil = ProgressDialogUtil(requireActivity())) { path, uri ->
                    requireActivity().getVideoThumbnailUri(uri)?.let {
                        viewModel.addVideoAttachment(
                            attachment = uri.toString(),
                            thumbnail = it.path ?: ""
                        )
                    }
                }
            }
            fabAddImage.setOnClickListener {
                pickCompressedImage(progressUtil = ProgressDialogUtil(requireActivity())) { path, uri ->
                    viewModel.addPhotoAttachment(attachment = path)
                }
            }
        }
    }

    private fun openFabMenu() {
        isFabOpen = true
        with(binding) {
            fabAddImage.visibility = View.VISIBLE
            fabAddVideo.visibility = View.VISIBLE
            fabAddImage.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    commonR.anim.show
                )
            )
            fabAddVideo.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    commonR.anim.show
                )
            )
            fabAttachmentMenu.setImageResource(commonR.drawable.ic_close_icon)
        }
    }

    private fun closeFabMenu() {
        isFabOpen = false
        with(binding) {
            fabAddImage.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    commonR.anim.hide
                )
            )
            fabAddVideo.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    commonR.anim.hide
                )
            )
            fabAddImage.visibility = View.GONE
            fabAddVideo.visibility = View.GONE
            fabAttachmentMenu.setImageResource(commonR.drawable.ic_menu)
        }
    }


}
