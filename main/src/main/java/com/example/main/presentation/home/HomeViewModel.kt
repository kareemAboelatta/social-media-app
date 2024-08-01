package com.example.main.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.data.local.UserPreferences
import com.example.core.ui.utils.DataState
import com.example.main.domain.model.Post
import com.example.main.domain.model.input.CreatePostInput
import com.example.main.domain.usecase.CreatePostUseCase
import com.example.main.domain.usecase.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val createPostUseCase: GetPostsUseCase
) : ViewModel() {

    private val _inputPost = MutableStateFlow(CreatePostInput())
    val input = _inputPost.asStateFlow()


    private val _postsResponse =
        MutableStateFlow<DataState<List<Post>>>(DataState.Idle)
    val postsResponse
        get() = _postsResponse.asStateFlow()


    fun fetchPosts() {
        viewModelScope.launch {
            createPostUseCase.invoke().collect {
                _postsResponse.value = it
            }
        }
    }

    val user = userPreferences.user
        .stateIn(viewModelScope, SharingStarted.Lazily, null)


}
