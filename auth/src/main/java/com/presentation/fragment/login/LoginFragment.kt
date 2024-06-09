package com.presentation.fragment.login

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.core.BaseFragment
import com.example.auth.R
import com.example.auth.databinding.FragmentLoginBinding
import com.example.common.ui.utils.MyValidation
import com.example.core.openMainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(inflate = FragmentLoginBinding::inflate) {

    private val viewModel by viewModels<LoginViewModel>()

    override fun onViewCreated() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.loginState.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED).collect { state ->
                state.handleState{
                    requireActivity().openMainActivity()
                }
            }
        }

        binding.loginBtnLogIn.setOnClickListener {
            val email: String = binding.inputTextLayoutEmail.editText!!.text.toString()
            val password: String = binding.inputTextLayoutPassword.editText!!.text.toString()

            if (MyValidation.isValidEmail(requireContext(), binding.inputTextLayoutEmail)
                && MyValidation.validatePass(requireContext(), binding.inputTextLayoutPassword)
            ) {
                viewModel.login(email, password)
            }
        }

        binding.loginBtnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginForget.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }
    }


}
