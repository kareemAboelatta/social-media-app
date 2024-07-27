package com.example.main.presentation.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.common.R
import com.example.core.BaseFragment
import com.example.core.ui.utils.loadCircleImageFromUrl
import com.example.main.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {


    private val viewModel by viewModels<HomeViewModel>()

    private val parentNavController by lazy {

        Navigation.findNavController(requireActivity(), R.id.container)
    }

    override fun onViewCreated() {

    }

    override fun onClicks() {
        with(binding) {
            etCaption.setOnClickListener {
                parentNavController.navigate(R.id.publishFragment)
            }
        }
    }

    override fun observers() {
        observeUserData()
    }

    private fun observeUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.user.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { user ->
                    user?.let {
                        val hint = getString(R.string.create_post_hint, it.name)
                        binding.etCaption.hint = hint
                        binding.userImage.loadCircleImageFromUrl(it.image)
                    }
                }
        }
    }

}