package com.example.socialmediaapp.auth.presentation.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.auth.domain.models.CreateUserInput
import com.example.socialmediaapp.auth.presentation.AuthViewModel
import com.example.common.ui.ProgressDialogUtil
import com.example.socialmediaapp.common.helpers.MyValidation
import com.example.common.ui.pickers.pickCompressedImage
import com.example.common.ui.utils.UIState
import com.example.socialmediaapp.databinding.FragmentRegisterBinding
import com.example.socialmediaapp.models.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {


    private var imagePickerLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            this.uri = it
            binding.regImage.setImageURI(uri)
        }
    }


    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!


    var uri: Uri? = null


    private val viewModel by viewModels<AuthViewModel>()

    private val progressDialogUtil = ProgressDialogUtil()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe createUserState
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createUserState.collect { state ->
                handleState(state)
            }
        }

        binding.regBtnRegister.setOnClickListener {
            val name = binding.inputTextLayoutName.editText?.text.toString()
            val bio = binding.inputTextLayoutBio.editText?.text.toString()
            val email = binding.inputTextLayoutEmail.editText?.text.toString()
            val password = binding.inputTextLayoutPassword.editText?.text.toString()

            if (valid()) {

                val createUserInput = CreateUserInput(
                    email = email,
                    password = password,
                    bio = bio,
                    image = uri!!,
                    name = name
                )

                viewModel.createUser(createUserInput)
            }


        }



        binding.regImage.setOnClickListener {
            pickCompressedImage(
                progressUtil = progressDialogUtil,
                onSaveFile = { compressedFile, uri ->
                    this.uri = uri
                    binding.regImage.setImageURI(uri)
                }
            )
        }


        binding.regBacktologin.setOnClickListener {
            findNavController().navigate(
                R.id.action_registerFragment_to_loginFragment
            )
        }

    }

    private fun handleState(state: UIState<User>) {
        when (state) {
            UIState.Empty -> {}
            is UIState.Error -> {
                progressDialogUtil.hideProgressDialog()
                Toast.makeText(activity, "" + state.error, Toast.LENGTH_SHORT).show()
            }

            UIState.Loading -> {
                progressDialogUtil.showProgressDialog(requireActivity())
            }

            is UIState.Success -> {
                Toast.makeText(context, "Registration success ", Toast.LENGTH_SHORT).show()
                progressDialogUtil.hideProgressDialog()
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }


    private fun valid(): Boolean {
        val bio = binding.inputTextLayoutBio.editText?.text.toString()

        if (!MyValidation.validateName(requireContext(), binding.inputTextLayoutName)) {
            return false
        } else if (bio.isEmpty()) {
            binding.inputTextLayoutBio.isHelperTextEnabled = true
            binding.inputTextLayoutBio.helperText = "Require*"
            return false
        } else if (!MyValidation.isValidEmail(requireContext(), binding.inputTextLayoutEmail)) {
            return false
        } else if (!MyValidation.validatePass(requireContext(), binding.inputTextLayoutPassword)) {
            return false
        } else if (uri == null) {
            Toast.makeText(activity, "Select image !!", Toast.LENGTH_LONG).show()
            return false
        } else {
            return true
        }
    }


}