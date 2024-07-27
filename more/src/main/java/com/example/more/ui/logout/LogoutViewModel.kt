package com.example.more.ui.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.ui.utils.DataState
import com.example.more.domain.usecases.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
   private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _logoutSuccess: MutableSharedFlow<DataState<Unit>> = MutableSharedFlow()
    val logoutSuccess = _logoutSuccess.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            logoutUseCase().onEach {
                _logoutSuccess.emit(it)
            } .collect()
        }

    }

}
