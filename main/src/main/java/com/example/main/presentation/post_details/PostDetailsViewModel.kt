package com.example.main.presentation.post_details

import androidx.lifecycle.ViewModel
import com.example.main.domain.repository.PostsRepository
import com.example.main.domain.usecase.GetPostDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postRepository: PostsRepository,
    private val postDetailsUseCase: GetPostDetailsUseCase
): ViewModel() {


}