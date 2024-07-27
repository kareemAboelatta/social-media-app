package com.example.more.ui.logout

import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.core.BaseDialogFragment
import com.example.core.openAuthActivity
import com.example.more.databinding.FragmentLogoutDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
                viewModel.logout()
            }
            btnDismiss.setOnClickListener {
                dismiss()
            }
        }
    }


    override fun observers() {
        observeLoggingOut()
    }
    private fun observeLoggingOut() {
       viewLifecycleOwner.lifecycleScope.launch {
           viewModel.logoutSuccess.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
               it.handleState {
                   requireActivity().openAuthActivity()
               }
           }
       }
    }

}
