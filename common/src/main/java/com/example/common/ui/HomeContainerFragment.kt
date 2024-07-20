package com.example.common.ui

import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.common.R
import com.example.common.databinding.FragmentHomeContainerBinding
import com.example.core.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeContainerFragment :
    BaseFragment<FragmentHomeContainerBinding>(FragmentHomeContainerBinding::inflate) {

    override fun onViewCreated() {}

    override fun onResume() {
        super.onResume()
        setupBottomNavigationView()
    }


    private fun setupBottomNavigationView() {
        val navController = Navigation.findNavController(requireActivity(), R.id.home_container)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        binding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            lifecycleScope.launch {
                initToolbarHome(
                    toolbar = binding.toolbar.layoutToolbar,
                    title = when (destination.id) {
                        R.id.homeFragment -> getString(R.string.home)
                        R.id.videosFragment -> getString(R.string.video)
                        R.id.profileFragment -> getString(R.string.profile)
                        R.id.moreFragment -> getString(R.string.more)
                        else -> ""
                    }
                )
            }
        }

    }
}