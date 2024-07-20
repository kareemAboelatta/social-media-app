package com.example.common.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.common.domain.model.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences @Inject constructor(context: Context) {

    private val dataStore = context.dataStore
    private val gson = Gson()
    private val USER_KEY = stringPreferencesKey("user")

    val user: Flow<User?> = dataStore.data
        .catch { exception ->
                emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[USER_KEY]?.let {
                gson.fromJson(it, User::class.java)
            }
        }

    suspend fun updateUser(user: User) {
        val userJson = gson.toJson(user)
        dataStore.edit { preferences ->
            preferences[USER_KEY] = userJson
        }
    }


/**
   Function to delete user data.
  - use it for logout
*/
    suspend fun deleteUserData() {
        dataStore.edit { preferences ->
            preferences.remove(USER_KEY)
        }
    }

/**
    Function to check if the user is logged in
*/
    fun isUserLoggedIn(): Flow<Boolean> = user.map { it != null }
}
