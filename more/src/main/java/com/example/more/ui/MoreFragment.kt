package com.example.more.ui

import androidx.navigation.Navigation
import com.example.core.BaseFragment
import com.example.common.R

import com.example.more.databinding.FragmentMoreBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreFragment :
    BaseFragment<FragmentMoreBinding>(FragmentMoreBinding::inflate) {

    private val parentNavController by lazy {

        Navigation.findNavController(requireActivity(), R.id.container)
    }

    override fun onViewCreated() {
        binding.recyclerView.apply {
            this.adapter = MoreAdapter(MoreItems.entries) { item ->
                parentNavController.navigate(item.destinationFragmentId)
            }
        }
    }

}