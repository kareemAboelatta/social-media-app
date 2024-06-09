package com.presentation.fragment.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.models.CreateUserInput
import com.domain.usecases.CreateUser
import com.example.common.domain.model.User
import com.example.core.ui.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val createUserUseCase: CreateUser,
) : ViewModel() {

    private val _createUserState = MutableSharedFlow<DataState<User>>()
    val createUserState get() = _createUserState.asSharedFlow()

    fun createUser(userInput: CreateUserInput) {
        viewModelScope.launch {
            createUserUseCase(userInput).collectLatest {
                _createUserState.emit(it)
            }
        }
    }

}
