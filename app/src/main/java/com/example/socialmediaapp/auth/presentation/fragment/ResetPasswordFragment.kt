package com.example.socialmediaapp.auth.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.socialmediaapp.auth.presentation.AuthViewModel
import com.example.common.ui.ProgressDialogUtil
import com.example.common.ui.utils.UIState
import com.example.socialmediaapp.databinding.FragmentResetPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {


    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!


    private val viewModel by viewModels<AuthViewModel>()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.resetBtnReset.setOnClickListener {
            val email =binding.resetEmail.text.toString()
            if (email.isNotEmpty()){
                viewModel.resetPassword(email =email )
            }else{
                Toast.makeText(context, "Write Your Email", Toast.LENGTH_SHORT).show()
            }

        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.resetPasswordState.collect{
                handleState(it)
            }
        }


    }

    private val progressDialogUtil =  ProgressDialogUtil()

    private fun handleState(state: UIState<Boolean>) {
        when (state) {
            UIState.Empty -> {}
            is UIState.Error -> {
                progressDialogUtil.hideProgressDialog()
                Toast.makeText(activity,  state.error, Toast.LENGTH_SHORT).show()
            }
            UIState.Loading ->{
                progressDialogUtil.showProgressDialog(requireActivity())
            }
            is UIState.Success -> {
                progressDialogUtil.hideProgressDialog()

                Toast.makeText(context, "Check your Email Now", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}