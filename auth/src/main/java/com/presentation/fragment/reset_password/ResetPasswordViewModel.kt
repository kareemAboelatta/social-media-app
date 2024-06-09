package com.presentation.fragment.reset_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.usecases.ResetPasswordUseCase
import com.example.core.ui.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : ViewModel() {


    private val _resetPasswordState = MutableSharedFlow<DataState<Boolean>>()
    val resetPasswordState get() = _resetPasswordState.asSharedFlow()

    fun resetPassword(email: String) {
        viewModelScope.launch {
            resetPasswordUseCase(email).collectLatest {
                _resetPasswordState.emit(it)
            }
        }
    }
}

