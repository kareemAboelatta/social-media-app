package com.presentation.fragment.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.models.CreateUserInput
import com.domain.usecases.CreateUserUseCase
import com.example.common.domain.model.User
import com.example.core.ui.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
) : ViewModel() {


    private val _inputState = MutableStateFlow(CreateUserInput())
    val inputState get() = _inputState.asStateFlow()


    private val _createUserState = MutableSharedFlow<DataState<User>>()
    val createUserState get() = _createUserState.asSharedFlow()

    fun createUser() {
        viewModelScope.launch {
            createUserUseCase(inputState.value).collectLatest {
                _createUserState.emit(it)
            }
        }
    }


    fun updateInput(
        name: String? = null,
        email: String? = null,
        bio: String? = null,
        password: String? = null,
        image: String? = null,
    ) {
        _inputState.update {
            it.copy(
                name = name ?: it.name,
                email = email ?: it.email,
                bio = bio ?: it.bio,
                password = password ?: it.password,
                image = image ?: it.image,
            )
        }
    }
}
