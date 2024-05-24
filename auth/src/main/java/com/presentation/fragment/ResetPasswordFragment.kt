package com.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.presentation.AuthViewModel
import com.example.auth.databinding.FragmentResetPasswordBinding
import com.example.core.ui.ProgressDialogUtil
import com.example.core.BaseFragment
import com.example.core.ui.utils.UIState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment<FragmentResetPasswordBinding>(FragmentResetPasswordBinding::inflate) {

    private val viewModel by viewModels<AuthViewModel>()

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
            viewModel.resetPasswordState.collect{
                it.handleState {
                    Toast.makeText(context, "Check your Email Now", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}