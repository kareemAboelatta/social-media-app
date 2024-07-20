package com.aait.moreui.logout

import androidx.lifecycle.ViewModel
import com.example.common.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    suspend fun logout() {
        userPreferences.deleteUserData()
    }

}
