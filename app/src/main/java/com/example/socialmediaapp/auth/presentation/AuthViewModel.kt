package com.example.socialmediaapp.auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.auth.domain.models.CreateUserInput
import com.example.socialmediaapp.auth.domain.usecases.CreateUser
import com.example.socialmediaapp.auth.domain.usecases.SignInWithEmailAndPassword
import com.example.socialmediaapp.common.utils.UIState
import com.example.socialmediaapp.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val createUserUseCase: CreateUser,
    private val signInWithEmailAndPassword: SignInWithEmailAndPassword,
)  : ViewModel() {



    private val _createUserState = MutableStateFlow<UIState<User>>(UIState.Empty)
    val createUserState: StateFlow<UIState<User>> get() = _createUserState



    fun createUser(userInput: CreateUserInput){
        _createUserState.value = UIState.Loading
        viewModelScope.launch {
            runCatching {
                createUserUseCase(userInput)
            }.onSuccess { user ->
                _createUserState.value = UIState.Success(user)

            }.onFailure { throwable ->
                Log.d(TAG, "createUser: ${throwable.localizedMessage.toString()}")
                _createUserState.value = UIState.Error(throwable.localizedMessage.toString())
            }
        }
    }





}