package com.example.more.ui.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.common.data.local.UserPreferences
import com.example.common.domain.model.User
import com.example.core.ui.utils.DataState
import com.example.more.domain.models.UpdateProfileInput
import com.example.more.domain.models.mapToUpdateProfileInput
import com.example.more.domain.usecases.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {


    private val _loginState = MutableSharedFlow<DataState<User>>()
    val loginState get() = _loginState.asSharedFlow()


    val user = userPreferences.user
        .stateIn(viewModelScope, SharingStarted.Lazily, null)



    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        userPreferences.updateUser(user)
    }


    // Holds the current user input for the profile update
    // This state gets updated as the user makes changes to the profile fields in the UI
    private val _updateInputState = MutableStateFlow(User())
    val updateInputState = _updateInputState.asStateFlow()

    // Holds the original profile data fetched from the server
    // This state is used to compare against the current user input to determine if any changes have been made
    private val _originalProfileState = MutableStateFlow(User())

    // Flow that emits a Boolean indicating whether the user input has changed compared to the original profile data
    // Combines _originalProfileState and _updateInputState and checks for inequality
    val isDataChanged: Flow<Boolean> =
        _originalProfileState.combine(_updateInputState) { original, current ->
            original != current
        }.distinctUntilChanged() // Ensures that only changes in the state result in emissions


    private val _updateProfileResponse =
        MutableStateFlow<DataState<User>>(DataState.Idle)
    val updateProfileResponse
        get() = _updateProfileResponse.asStateFlow()






    fun updateProfile() {
        viewModelScope.launch {
            val input = updateInputState.value
            updateProfileUseCase(input).collectLatest {
                _updateProfileResponse.emit(it)
            }
        }
    }

    fun setProfileOriginalData(user: User) {
        _originalProfileState.update {
            user
        }
        _updateInputState.update {
            user
        }
    }

    fun updateInputState(
        name: String? = null,
        bio: String? = null,
        image: String? = null
    ) {
        _updateInputState.update { currentState ->
            currentState.copy(
                name = name ?: currentState.name,
                bio = bio ?: currentState.bio,
                image = image ?: currentState.image,
            )
        }
    }


}


