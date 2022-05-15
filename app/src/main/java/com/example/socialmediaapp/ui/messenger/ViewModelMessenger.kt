package com.example.socialmediaapp.ui.messenger

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.models.Chat
import com.example.socialmediaapp.models.User
import com.example.socialmediaapp.repository.RepositoryMessenger
import com.example.socialmediaapp.utils.Constants
import com.example.socialmediaapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject


@HiltViewModel
class ViewModelMessenger @Inject constructor(
    private val repository: RepositoryMessenger,
    val context: Context,
    val auth: FirebaseAuth,
    val refDatabase: DatabaseReference,
    val ref: DatabaseReference
) : ViewModel(){


    private val _peopleAll: MutableLiveData<List<User>> = MutableLiveData()
    val peopleAll: LiveData<List<User>> get() = _peopleAll

    private val _currentUser: MutableLiveData<User> = MutableLiveData()
    val currentUser: LiveData<User> get() = _currentUser




    private fun getPeople(){
        val friendsList= mutableListOf<User>()
        ref.child("users")
            .get().addOnSuccessListener { friends->
                val data=friends.children
                data.forEach { friend ->
                    val x= friend.getValue<User>()
                    if (x?.id != auth.currentUser?.uid!!)
                        friendsList.add(x!!)
                }
                _peopleAll.value=friendsList
            }
    }


    fun sendMessage(receiver: User, messageBody: String)=repository.sendMessage(receiver,messageBody)

    fun loadChat(userID: String) =repository.loadChat(userID)





    private var chatsLiveData=MutableLiveData<Resource<List<Chat>>>()
    private var currentUserLiveData=MutableLiveData<Resource<User>>()
    private var userLiveData=MutableLiveData<Resource<User>>()

    private fun getChats(){
        chatsLiveData=repository.getChats()
    }


    fun getUser(id:String)
    {
        userLiveData=repository.getUser(id)
    }
    fun loadChats()=chatsLiveData
    fun user()=userLiveData



    private fun getCurrentUser(){
            refDatabase.child(Constants.USERS)
                .child(auth.currentUser?.uid!!)
                .get().addOnSuccessListener { snapShot->
                    val user=snapShot.getValue(User::class.java)
                    _currentUser.value= user!!
                }.addOnFailureListener {
                    Toast.makeText(context, ""+it.message.toString(), Toast.LENGTH_SHORT).show()
                }


    }

    init {
        viewModelScope.launch {
            launch {
                getChats()
            }
            launch {
                getPeople()
            }
            launch {
                getCurrentUser()
            }
        }

    }

}