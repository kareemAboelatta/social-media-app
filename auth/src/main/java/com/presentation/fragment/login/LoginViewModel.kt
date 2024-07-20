package com.presentation.fragment.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.usecases.SignInUseCase
import com.example.common.data.local.UserPreferences
import com.example.common.domain.model.User
import com.example.core.ui.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCaseUseCase: SignInUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {


    private val _loginState = MutableSharedFlow<DataState<User>>()
    val loginState get() = _loginState.asSharedFlow()



    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        userPreferences.updateUser(user)
    }


    fun login(email: String, password: String) {
        viewModelScope.launch {
            signInUseCaseUseCase(email, password).collectLatest {
                _loginState.emit(it)
            }
        }
    }


}
