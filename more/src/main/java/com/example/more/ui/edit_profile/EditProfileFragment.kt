package com.example.more.ui.edit_profile

import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.core.BaseFragment
import com.example.core.domain.utils.ValidationException
import com.example.core.ui.pickers.pickCompressedImage
import com.example.core.ui.utils.DataState
import com.example.more.R
import com.example.more.databinding.FragmentEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EditProfileFragment :
    BaseFragment<FragmentEditProfileBinding>(FragmentEditProfileBinding::inflate) {

    private val viewModel by viewModels<EditProfileViewModel>()


    override fun onViewCreated() {
        initToolbar(
            binding.toolbar.layoutToolbar,
            getString(R.string.edit_profile),
        )

    }


    override fun onClicks() {
        with(binding) {
            ivProfile.setOnClickListener {
                pickCompressedImage(
                    progressUtil = progressDialogUtil,
                    onSaveFile = { imagePath, _ ->
                        viewModel.updateInputState(
                            image = imagePath,
                        )
                    }
                )
            }


            etName.addTextChangedListener {
                viewModel.updateInputState(name = it.toString())
            }

            etBio.addTextChangedListener {
                viewModel.updateInputState(
                    bio = it.toString()
                )
            }

            btnConfirm.setOnClickListener {
                viewModel.updateProfile()
            }
        }

    }


/*
    override fun observers() {
        observeGetProfile()
        observeInputState()
        registerObserver()
        observeUserChange()
    }

    private fun observeUserChange() {
        observeRegularFlow(viewModel.isDataChanged) { isDataChanged ->
            binding.btnConfirm.apply {
                isEnabled = isDataChanged
                binding.btnConfirm.setBackgroundColor(
                    if (isDataChanged)
                        ContextCompat.getColor(requireActivity(), R.color.colorPrimary)
                    else
                        ContextCompat.getColor(requireActivity(), R.color.gray_mid)
                )
            }
        }
    }

    private fun observeInputState() {
        observeRegularFlow(viewModel.updateInputState) {
            binding.apply {
                it.image?.let { ivProfile.loadImageFromUrl(it) }
                it.selectedCity?.let { etCity.setText(it.name) }
                it.selectedCity?.let { etCity.setText(it.name) }
                etEmail.setTextKeepState(it.email)
                etName.setTextKeepState(it.name)
            }
        }
    }

    private fun observeGetProfile() {
        observeDataStateFlow(viewModel.profileResponse) {
            it.data?.let { profile ->
                viewModel.setProfileOriginalData(profile)
            }
        }
    }
*/


    private fun observeRegisterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateProfileResponse.flowWithLifecycle(lifecycle).collectLatest { state ->
                when (state) {
                    is DataState.Error -> {
                        when (state.throwable) {
                            is ValidationException.InvalidEmptyNameException -> {
                                showErrorToast(com.example.common.R.string.name_required)
                            }

                            is ValidationException.InvalidNameException -> {
                                showErrorToast(com.example.common.R.string.name_invalid)
                            }

                            is ValidationException.InvalidEmptyBioException -> {
                                showErrorToast(com.example.common.R.string.bio_required)
                            }

                            is ValidationException.InvalidBioException -> {
                                showErrorToast(com.example.common.R.string.bio_invalid)
                            }

                            is ValidationException.InvalidEmptyEmailException -> {
                                showErrorToast(com.example.common.R.string.email_required)
                            }

                            is ValidationException.InvalidEmailException -> {
                                showErrorToast(com.example.common.R.string.email_invalid)
                            }

                            is ValidationException.InvalidEmptyPasswordException -> {
                                showErrorToast(com.example.common.R.string.password_required)
                            }

                            is ValidationException.InvalidPasswordException -> {
                                showErrorToast(com.example.common.R.string.password_invalid)
                            }

                            is ValidationException.InvalidEmptyImageException -> {
                                showErrorToast(com.example.common.R.string.image_required)
                            }

                            else -> {
                                state.handleState()
                            }
                        }
                    }

                    else -> {
                        state.handleState {
                            showToast(com.example.common.R.string.data_updated)
                        }
                    }
                }
            }
        }
    }


/*    private fun updateLocalDataAndNavigate(userModel: UserModel) {
        lifecycleScope.launch {
            viewModel.saveUserData(
                userModel = userModel
            )
            navToBack()
        }
    }*/


    private fun navToBack() {
        findNavController().popBackStack()
    }

}