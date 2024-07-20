package com.example.main.presentation.publish_post

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.aboelatta.universalMediaPreview.MediaPreviewAttachment
import com.aboelatta.universalMediaPreview.MediaPreviewType
import com.aboelatta.universalMediaPreview.PreviewAttachmentDialogBuilder
import com.example.common.domain.model.AttachmentType
import com.example.core.BaseFragment
import com.example.core.ui.utils.ProgressDialogUtil
import com.example.core.ui.pickers.pickCompressedImage
import com.example.core.ui.pickers.pickCompressedVideo
import com.example.core.ui.utils.loadCircleImageFromUrl
import com.example.main.databinding.FragmentPublishPostBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.common.R as commonR

@AndroidEntryPoint
class PublishPostFragment :
    BaseFragment<FragmentPublishPostBinding>(FragmentPublishPostBinding::inflate) {

    private var isFabOpen = false
    private val viewModel by viewModels<PublishPostViewModel>()
    private lateinit var attachmentAdapter: AttachmentsAdapter

    override fun onViewCreated() {
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    override fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uploadPostResponse.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    it.handleState {
                        Toast.makeText(requireActivity(), "Post Published", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


    private fun setupRecyclerView() {
        attachmentAdapter = AttachmentsAdapter(
            onAttachmentClicked = { _, position ->
                showPreviewAttachmentDialog(position)
            },
            onRemoveAttachment = { attachment ->
                viewModel.deleteSelectedAttachment(attachment)
            }
        )
        binding.rvAttachments.adapter = attachmentAdapter
    }

    private fun showPreviewAttachmentDialog(position: Int) {
        val mediaPreviewAttachments = viewModel.input.value.attachments.map {
            MediaPreviewAttachment(
                type = if (it.type == AttachmentType.VIDEO) MediaPreviewType.VIDEO else MediaPreviewType.IMAGE,
                attachment = it.attachment
            )
        }
        PreviewAttachmentDialogBuilder(requireActivity())
            .setStartPosition(position)
            .setAttachments(mediaPreviewAttachments)
            .show(parentFragmentManager)
    }

    private fun setupObservers() {
        observeUserData()
        observeAttachments()
    }

    private fun observeUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.user.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { user ->
                    user?.let {
                        viewModel.updatePostInput(
                            userId = it.id,
                            name = it.name,
                            bio = it.bio,
                            image = it.image
                        )
                    }
                }
        }
    }

    private fun observeAttachments() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.input.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { input ->
                    attachmentAdapter.submitAttachmentsList(input.attachments)
                    binding.etCaption.setTextKeepState(input.caption)
                    binding.userImage.loadCircleImageFromUrl(input.user.image)
                    binding.userName.text = input.user.name
                    binding.userBio.text = input.user.bio
                    val hint =
                        getString(com.example.common.R.string.create_post_hint, input.user.name)
                    binding.etCaption.hint = hint
                    binding.etCaption.requestFocus()

                }
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            fabAttachmentMenu.setOnClickListener {
                toggleFabMenu()
            }

            fabAddVideo.setOnClickListener {
                pickCompressedVideo(ProgressDialogUtil(requireActivity())) { path, _ ->
                    viewModel.addVideoAttachment(path)
                }
            }

            fabAddImage.setOnClickListener {
                pickCompressedImage(ProgressDialogUtil(requireActivity())) { path, _ ->
                    viewModel.addPhotoAttachment(path)
                }
            }

            etCaption.doAfterTextChanged { text ->
                viewModel.updatePostInput(caption = text.toString())
            }

            btnPublish.setOnClickListener {
                viewModel.createPost()
            }
        }
    }

    private fun toggleFabMenu() {
        if (isFabOpen) {
            closeFabMenu()
        } else {
            openFabMenu()
        }
    }

    private fun openFabMenu() {
        isFabOpen = true
        with(binding) {
            fabAddImage.apply {
                visibility = View.VISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), commonR.anim.show))
            }
            fabAddVideo.apply {
                visibility = View.VISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), commonR.anim.show))
            }
            fabAttachmentMenu.setImageResource(commonR.drawable.ic_close_icon)
        }
    }

    private fun closeFabMenu() {
        isFabOpen = false
        with(binding) {
            fabAddImage.apply {
                startAnimation(AnimationUtils.loadAnimation(requireContext(), commonR.anim.hide))
                visibility = View.GONE
            }
            fabAddVideo.apply {
                startAnimation(AnimationUtils.loadAnimation(requireContext(), commonR.anim.hide))
                visibility = View.GONE
            }
            fabAttachmentMenu.setImageResource(commonR.drawable.ic_menu)
        }
    }
}
