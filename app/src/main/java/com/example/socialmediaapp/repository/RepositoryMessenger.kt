package com.example.socialmediaapp.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.socialmediaapp.models.*
import com.example.socialmediaapp.network.RetrofitBuilder
import com.example.socialmediaapp.utils.Constants
import com.example.socialmediaapp.utils.Resource
import com.example.socialmediaapp.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "chatKareem"

class RepositoryMessenger @Inject constructor(
    private var refDatabase: DatabaseReference,
    private var auth: FirebaseAuth,
    private var context: Context
) {

    private val uid = auth.currentUser?.uid.toString()


    fun loadChat(userID: String): MutableLiveData<Resource<List<Message>>> {
        val chatID = Utils.getChatID(uid, userID)
        val mLiveData = MutableLiveData<Resource<List<Message>>>()
        refDatabase.child(Constants.CHAT_MESSAGES)
            .child(chatID)
            .child(Constants.MESSAGES)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messagesList = mutableListOf<Message>()
                    snapshot.children.forEach {
                        it.getValue(Message::class.java)?.let { msg ->
                            messagesList.add(msg)
                        }
                    }
                    markAsSeen(chatID)
                    mLiveData.value = Resource.success(messagesList)
                }

                override fun onCancelled(error: DatabaseError) {
                    mLiveData.value = Resource.error(error.message, null)
                }
            })
        return mLiveData
    }




    private fun markAsSeen(chatID: String) {
        refDatabase.
        child("${Constants.CHATS}/$uid/$chatID/${Constants.LAST_MESSAGE}")
            .child("seen")
            .setValue(true)
    }

    fun sendMessage(receiver: User, messageBody: String): MutableLiveData<Resource<Unit>> {
        val mLiveData = MutableLiveData<Resource<Unit>>()
        val ref = refDatabase
        val chatID = Utils.getChatID(uid, receiver.id )
        val messageID = System.currentTimeMillis().toString()

        ref.child(Constants.CHAT_MESSAGES)
            .child(chatID)
            .child(Constants.MESSAGES)
            .child(messageID)
            .setValue(Message(messageBody,messageID,uid,receiver.id)).addOnSuccessListener {
                mLiveData.value = Resource.success(null)

                updateSenderChatLastMessage(receiver.id,chatID, messageBody, messageID)
                updateReceiverChatLastMessage(receiver.id, chatID, messageBody, messageID)
                sendNotification(receiver, messageBody)


            }.addOnFailureListener {
                mLiveData.value = Resource.error(it.message.toString(), null)
            }
        return mLiveData
    }


    private val languageIdentifierLiveData=MutableLiveData<Resource<String>>()
    private val languageIdentifier = LanguageIdentification.getClient()
    suspend fun identifyLanguage(text: String):MutableLiveData<Resource<String>> {
        var language = "und"

        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->

                language = if (languageCode == "und") {
                    languageCode
                } else {
                    TranslateLanguage.fromLanguageTag(languageCode).toString()
                }

                languageIdentifierLiveData.value= Resource.success(language)
            }
            .addOnFailureListener {
                languageIdentifierLiveData.value= Resource.error(it.message.toString(),language)

            }
        return  languageIdentifierLiveData
    }












    private fun updateSenderChatLastMessage(
        receiverID: String,
        chatID: String,
        messageBody: String,
        messageID: String
    ) {
        refDatabase.child(Constants.CHATS)
            .child(uid)
            .child(chatID)
            .child(Constants.LAST_MESSAGE)
            .setValue(Chat(userID = receiverID, lastMessage = messageBody, time = messageID,seen = true))
    }

    private fun updateReceiverChatLastMessage(
        receiverID: String,
        chatID: String,
        messageBody: String,
        messageID: String
    ) {
        refDatabase.child(Constants.CHATS)
            .child(receiverID)
            .child(chatID)
            .child(Constants.LAST_MESSAGE)
            .setValue(Chat(userID = uid, lastMessage = messageBody, time = messageID,seen = false))
    }

    private fun sendNotification(receiver: User, messageBody: String) {
        val ref = refDatabase.child("users/$uid")
        ref.get().addOnSuccessListener { data ->
            val sender = data.getValue(User::class.java)
            sender?.let {
                val notification = PushNotification(
                    NotificationData("${it.name}-${it.id}", messageBody), receiver.token
                )
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitBuilder.api.postNotification(notification)
                        if (response.isSuccessful) {
                            Log.d(TAG, "Response: $response")
                        } else {
                            Log.d(TAG, response.errorBody().toString())
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, e.message.toString());
                    }
                }
            }
        }
    }


    private val mLiveData = MutableLiveData<Resource<List<Chat>>>()
    private val userLiveData=MutableLiveData<Resource<User>>()

    fun getChats(): MutableLiveData<Resource<List<Chat>>> {
        mLiveData.value= Resource.loading(null)
        refDatabase.child(Constants.CHATS)
            .child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mp = mutableMapOf<String, Chat>()
                    snapshot.children.forEach { child ->
                        child.child(Constants.LAST_MESSAGE).getValue(Chat::class.java)?.let { chat ->
                            mp[chat.userID] = chat
                        }
                    }
                    getChatsInformation(mp)
                }

                override fun onCancelled(error: DatabaseError) {
                    mLiveData.value = Resource.error(error.message, null)

                }

            })
        return mLiveData
    }

    private fun getChatsInformation(mp: MutableMap<String, Chat>) {
        val mChats = mutableListOf<Chat>()
        refDatabase.child(Constants.USERS)
            .get().addOnSuccessListener {
                it.children.forEach { user ->
                    val currentUser = user.getValue(User::class.java)
                    currentUser?.let { _user ->
                        if (mp.contains(_user.id)) {
                            mChats.add(
                                Chat(
                                    _user.id,
                                    _user.image,
                                    _user.name,
                                    _user.token,
                                    mp[_user.id]?.lastMessage,
                                    mp[_user.id]!!.time,
                                    mp[_user.id]!!.seen
                                )
                            )
                        }
                    }
                }
                mChats.sortByDescending { chat -> chat.time }
                mLiveData.value = Resource.success(mChats)
            }.addOnFailureListener {
                mLiveData.value = Resource.error(it.message.toString(), null)
            }
    }


    private val currentLiveData=MutableLiveData<Resource<User>>()
    fun getCurrentUserData(): MutableLiveData<Resource<User>> {
        refDatabase.child(Constants.USERS)
            .child(uid)
            .get().addOnSuccessListener { snapShot->
                val user=snapShot.getValue(User::class.java)
                currentLiveData.value= Resource.success(user)
            }.addOnFailureListener {
                currentLiveData.value= Resource.error(it.message.toString(),null)
            }
        return currentLiveData
    }

    fun getUser(uid: String ): MutableLiveData<Resource<User>> {
        refDatabase.child(Constants.USERS)
            .child(uid)
            .get().addOnSuccessListener { snapShot->
                val user=snapShot.getValue(User::class.java)
                userLiveData.value= Resource.success(user)
            }.addOnFailureListener {
                userLiveData.value= Resource.error(it.message.toString(),null)
            }
        return userLiveData
    }

}