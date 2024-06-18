package com.presentation.fragment.login

import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.auth.R
import com.example.auth.databinding.FragmentLoginBinding
import com.example.core.BaseFragment
import com.example.core.domain.utils.ValidationException
import com.example.core.openMainActivity
import com.example.core.ui.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.common.R as CommonR

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(inflate = FragmentLoginBinding::inflate) {

    private val viewModel by viewModels<LoginViewModel>()

    override fun onViewCreated() {}

    override fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginState.flowWithLifecycle(lifecycle)
                .collectLatest { state ->
                    if (state is DataState.Error) {
                        when (state.throwable) {
                            is ValidationException.InvalidEmptyEmailException -> {
                                showErrorToast(CommonR.string.email_required)
                            }

                            is ValidationException.InvalidEmailException -> {
                                showErrorToast(CommonR.string.email_invalid)
                            }

                            is ValidationException.InvalidEmptyPasswordException -> {
                                showErrorToast(CommonR.string.password_required)
                            }

                            is ValidationException.InvalidPasswordException -> {
                                showErrorToast(CommonR.string.password_invalid)
                            }

                            else -> {
                                state.handleState()
                            }
                        }
                    } else {
                        state.handleState {
                            requireActivity().openMainActivity()
                        }
                    }

                }
        }

    }


    override fun onClicks() {

        binding.loginBtnLogIn.setOnClickListener {
            val email: String = binding.inputTextLayoutEmail.editText!!.text.toString()
            val password: String = binding.inputTextLayoutPassword.editText!!.text.toString()
            viewModel.login(email, password)
        }

        binding.loginBtnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginForget.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }
    }

}
