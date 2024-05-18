package com.example.socialmediaapp.auth.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.auth.presentation.AuthViewModel
import com.example.socialmediaapp.common.helpers.MyValidation
import com.example.socialmediaapp.ui.main.MainActivity
import com.example.core.ui.ProgressDialogUtil
import com.example.common.ui.utils.UIState
import com.example.core.BaseFragment
import com.example.socialmediaapp.databinding.FragmentLoginBinding
import com.example.socialmediaapp.models.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(inflate = FragmentLoginBinding::inflate) {

    private val viewModel by viewModels<AuthViewModel>()

    override fun onViewCreated() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.signInUserState.collect { state ->
                handleState(state)
            }
        }

        binding.loginBtnLogIn.setOnClickListener {
            val email: String = binding.inputTextLayoutEmail.editText!!.text.toString()
            val password: String = binding.inputTextLayoutPassword.editText!!.text.toString()

            if (MyValidation.isValidEmail(requireContext(), binding.inputTextLayoutEmail)
                && MyValidation.validatePass(requireContext(), binding.inputTextLayoutPassword)
            ) {
                viewModel.signInWithEmailAndPassword(email, password)
            }
        }

        binding.loginBtnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginForget.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }    }


    private fun handleState(state: UIState<User>) {

        when (state) {
            UIState.Empty -> {}
            is UIState.Error -> {
                progressDialogUtil.hideProgress()

                Toast.makeText(context, "" + state.error, Toast.LENGTH_SHORT).show()
            }

            UIState.Loading -> {
                progressDialogUtil.showProgress()
            }
            is UIState.Success -> {
                progressDialogUtil.hideProgress()

                activity?.startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()


            }
        }


    }

}
