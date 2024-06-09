package com.presentation.fragment.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.usecases.SignInWithEmailAndPassword
import com.example.common.domain.model.User
import com.example.core.ui.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPassword,
) : ViewModel() {


    private val _loginState = MutableSharedFlow<DataState<User>>()
    val loginState get() = _loginState.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            signInWithEmailAndPasswordUseCase(email, password).collectLatest {
                _loginState.emit(it)
            }
        }
    }


}
