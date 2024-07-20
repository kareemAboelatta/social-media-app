package com.example.more.ui.logout

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aait.moreui.logout.LogoutViewModel
import com.example.core.BaseDialogFragment
import com.example.core.openAuthActivity
import com.example.more.databinding.FragmentLogoutDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LogoutDialogFragment :
    BaseDialogFragment<FragmentLogoutDialogBinding>(FragmentLogoutDialogBinding::inflate) {

    private val viewModel by viewModels<LogoutViewModel>()

    override fun onViewCreated() {
        isCancelable = false

    }

    override fun onClicks() {
        with(binding) {
            btnLogout.setOnClickListener {
                handleLogout()
            }
            btnDismiss.setOnClickListener {
                dismiss()
            }
        }
    }


    private fun handleLogout() {
        lifecycleScope.launch {
            viewModel.logout()
            requireActivity().openAuthActivity()
        }
    }

}
