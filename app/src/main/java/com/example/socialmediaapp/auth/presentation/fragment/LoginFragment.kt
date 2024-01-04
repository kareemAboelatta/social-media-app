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
import com.example.socialmediaapp.auth.presentation.ViewModelSignUser
import com.example.socialmediaapp.common.utils.Status
import com.example.socialmediaapp.common.utils.UIState
import com.example.socialmediaapp.databinding.FragmentLoginBinding
import com.example.socialmediaapp.models.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

//    private val viewModel by viewModels<ViewModelSignUser>()

    private val viewModel by viewModels<AuthViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
        }
    }


    private fun handleState(state: UIState<User>) {
        when (state) {
            UIState.Empty -> {}
            is UIState.Error -> {
                binding.progress.visibility = View.INVISIBLE
                Toast.makeText(activity, "" + state.error, Toast.LENGTH_SHORT).show()
            }

            UIState.Loading -> binding.progress.visibility = View.VISIBLE
            is UIState.Success -> {
                binding.progress.visibility = View.GONE
                activity?.startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()


            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
