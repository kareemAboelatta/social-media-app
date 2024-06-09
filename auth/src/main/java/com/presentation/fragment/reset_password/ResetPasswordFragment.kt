package com.presentation.fragment.reset_password

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.auth.databinding.FragmentResetPasswordBinding
import com.example.core.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment<FragmentResetPasswordBinding>(FragmentResetPasswordBinding::inflate) {

    private val viewModel by viewModels<ResetPasswordViewModel>()

    override fun onViewCreated() {
        binding.resetBtnReset.setOnClickListener {
            val email = binding.resetEmail.text.toString()
            if (email.isNotEmpty()){
                viewModel.resetPassword(email = email )
            }else{
                Toast.makeText(context, "Write Your Email", Toast.LENGTH_SHORT).show()
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.resetPasswordState.collectLatest{
                it.handleState {
                    Toast.makeText(context, "Check your Email Now", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}