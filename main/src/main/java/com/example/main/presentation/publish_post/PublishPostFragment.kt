package com.example.main.presentation.publish_post

import androidx.fragment.app.viewModels
import com.example.core.BaseFragment
import com.example.core.ui.adapter.AddDeleteImagesAdapter
import com.example.main.databinding.FragmentPublishPostBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PublishPostFragment : BaseFragment<FragmentPublishPostBinding>(FragmentPublishPostBinding::inflate) {


    private val viewModel: PublishPostViewModel by viewModels()
    private lateinit var attachmentAdapter: AddDeleteImagesAdapter
    override fun onViewCreated() {
        attachmentAdapter = AddDeleteImagesAdapter(
            removeUriImageOnClick = { _, position ->

            },
            onImageClicked = {

            },
            addImageOnClick = {

            }
        )

    }

}
