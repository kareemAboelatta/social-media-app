package com.example.socialmediaapp.ui.main

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.models.Comment
import com.example.socialmediaapp.models.Post
import com.example.socialmediaapp.models.User
import com.example.socialmediaapp.repository.Repository
import com.example.socialmediaapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelMain  @Inject constructor(
    private val repository: Repository,
    val context: Context,
    val auth: FirebaseAuth,
    ) : ViewModel() {




    private var uploadState= MutableLiveData<Resource<String>>()
    fun uploadToken(){
        uploadState=repository.uploadToken()
    }
    fun updateUserStatus(status:String)=repository.updateUserStatus(status)
    fun getUploadState()=uploadState


    var specificUserLiveData=MutableLiveData<Resource<User>>()
    fun getSpecificUserData(userID:String){
        viewModelScope.launch {
            specificUserLiveData=repository.getSpecificUserData(userID)
        }
    }



    var currentUserLiveData=MutableLiveData<Resource<User>>()
    fun getDataForCurrentUser() {
        viewModelScope.launch {
            currentUserLiveData= repository.getCurrentUserData()
        }
    }



    var languageIdentifierLiveData=MutableLiveData<Resource<String>>()
    fun identifyLanguage(text: String){
        viewModelScope.launch {
            languageIdentifierLiveData=repository.identifyLanguage(text)
        }
    }

    var postLiveData = MutableLiveData<Resource<Boolean>>()
    fun uploadPost(post: Post){
        viewModelScope.launch {
            postLiveData=repository.uploadPost(post)
        }
    }


    var changeNameOrBioLiveData= MutableLiveData<Resource<Boolean>>()
    fun changeNameOrBio(value: String,key: String){
        viewModelScope.launch {
            changeNameOrBioLiveData=repository.changeNameOrBio(value,key)
        }
    }

    var changePhotoOrCoverLiveData=MutableLiveData<Resource<Boolean>>()
    fun changePhotoOrCover(uri: Uri,keyInStorage: String,keyInDB: String){
        viewModelScope.launch {
            changePhotoOrCoverLiveData=repository.changePhotoOrCover(uri,keyInStorage,keyInDB)
        }
    }

    var postsForSpecificUserLiveData=MutableLiveData<Resource<List<Post>>>()
    fun getPostsForSpecificUser(userID:String){
        viewModelScope.launch {
            postsForSpecificUserLiveData=repository.getPostsForSpecificUser(userID)
        }
    }

    var videoOnlyLiveData=MutableLiveData<Resource<List<Post>>>()
    fun getVideosOnly(){
        viewModelScope.launch {
            videoOnlyLiveData=repository.getVideosOnly()
        }
    }

    var postsLiveData=MutableLiveData<Resource<List<Post>>>()
    fun getPosts(){
        viewModelScope.launch {
            postsLiveData=repository.getPosts()
        }
    }


    var commentsLiveData=MutableLiveData<Resource<List<Comment>>>()
    fun loadComments(postId:String){
        viewModelScope.launch {
            commentsLiveData=repository.loadComments(postId)
        }

    }

    fun setLike(post: Post){
        viewModelScope.launch(Dispatchers.IO) {
            repository.setLike(post)
        }
    }

    fun postComment(post:Post,comment: Comment){
        CoroutineScope(Dispatchers.Default).launch {
            repository.postComment(post, comment)
        }
    }


}