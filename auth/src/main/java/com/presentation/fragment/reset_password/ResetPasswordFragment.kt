package com.presentation.fragment.reset_password

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.auth.databinding.FragmentResetPasswordBinding
import com.example.core.BaseFragment
import com.example.core.domain.utils.ValidationException
import com.example.core.ui.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.common.R as CommonR

@AndroidEntryPoint
class ResetPasswordFragment :
    BaseFragment<FragmentResetPasswordBinding>(FragmentResetPasswordBinding::inflate) {

    private val viewModel by viewModels<ResetPasswordViewModel>()

    override fun onViewCreated() {}

    override fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.resetPasswordState.collectLatest {
                if (it is DataState.Error) {
                    when (it.throwable) {
                        is ValidationException.InvalidEmptyNameException ->
                            showErrorToast(CommonR.string.email_required)
                        is ValidationException.InvalidEmailException ->
                            showErrorToast(CommonR.string.email_invalid)
                        else -> it.handleState()
                    }
                } else {
                    it.handleState {
                        showToast(CommonR.string.check_email)
                    }
                }
            }
        }
    }


    override fun onClicks() {
        binding.resetBtnReset.setOnClickListener {
            val email = binding.resetEmail.text.toString()
            if (email.isNotEmpty()) {
                viewModel.resetPassword(email = email)
            } else {
                Toast.makeText(context, "Write Your Email", Toast.LENGTH_SHORT).show()
            }
        }
    }


}