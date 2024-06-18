package com.presentation.fragment.register

import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.core.BaseFragment
import com.example.core.ui.pickers.pickCompressedImage
import com.example.auth.R
import com.example.common.R as CommonR
import com.example.auth.databinding.FragmentRegisterBinding
import com.example.core.domain.utils.ValidationException
import com.example.core.ui.utils.DataState
import com.example.core.ui.utils.loadCircleImageFromUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentRegisterBinding>(inflate = FragmentRegisterBinding::inflate) {

    private val viewModel by viewModels<RegisterViewModel>()

    override fun onViewCreated() {}

    override fun onClicks() {
        binding.regBtnRegister.setOnClickListener {
            viewModel.updateInput(
                name = binding.inputTextName.text.toString(),
                email = binding.inputTextEmail.text.toString(),
                password = binding.inputTextPassword.text.toString(),
                bio = binding.inputTextBio.text.toString(),
            )
            viewModel.createUser()
        }

        binding.regImage.setOnClickListener {
            pickCompressedImage(
                progressUtil = progressDialogUtil,
                onSaveFile = { image, _ ->
                    viewModel.updateInput(image = image)
                }
            )
        }


        binding.regBacktologin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

    }


    override fun observers() {
        super.observers()
        observeRegisterState()
        observeUserInputState()
    }

    private fun observeUserInputState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.inputState.flowWithLifecycle(lifecycle).collectLatest {
                with(binding){
                    inputTextName.setTextKeepState(it.name)
                    inputTextBio.setTextKeepState(it.bio)
                    inputTextEmail.setTextKeepState(it.email)
                    inputTextPassword.setTextKeepState(it.password)
                    regImage.loadCircleImageFromUrl(it.image)
                }
            }
        }
    }
    private fun observeRegisterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createUserState.flowWithLifecycle(lifecycle).collectLatest { state ->
                when (state) {
                    is DataState.Error -> {
                        when (state.throwable) {
                            is ValidationException.InvalidEmptyNameException -> {
                                showErrorToast(CommonR.string.name_required)
                            }
                            is ValidationException.InvalidNameException -> {
                                showErrorToast(CommonR.string.name_invalid)
                            }
                            is ValidationException.InvalidEmptyBioException -> {
                                showErrorToast(CommonR.string.bio_required)
                            }
                            is ValidationException.InvalidBioException -> {
                                showErrorToast(CommonR.string.bio_invalid)
                            }
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
                            is ValidationException.InvalidEmptyImageException -> {
                                showErrorToast(CommonR.string.image_required)
                            }
                            else -> {
                                state.handleState()
                            }
                        }
                    }
                    else -> {
                        state.handleState {
                            showToast(CommonR.string.registration_success)
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                    }
                }
            }
        }
    }


}