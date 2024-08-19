package com.example.main.presentation.post_details

import androidx.fragment.app.viewModels
import com.example.core.BaseFragment
import com.example.main.databinding.FragmentPostDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailsFragment :
    BaseFragment<FragmentPostDetailsBinding>(inflate = FragmentPostDetailsBinding::inflate) {
    private val viewModel: PostDetailsViewModel by viewModels()
    override fun onViewCreated() {

    }

}