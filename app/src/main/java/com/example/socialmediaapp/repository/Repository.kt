package com.example.socialmediaapp.repository

import android.content.Context
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.socialmediaapp.R
import com.example.socialmediaapp.models.Comment
import com.example.socialmediaapp.models.Post
import com.example.socialmediaapp.models.User
import com.example.common.ui.utils.Constants
import com.example.common.ui.utils.Resource





import com.google.firebase.auth.auth
import com.google.firebase.database.*


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage

import javax.inject.Inject
import kotlin.collections.ArrayList

class Repository @Inject constructor(
    var refDatabase: DatabaseReference,
    private var refStorage: StorageReference,
    private var auth: FirebaseAuth,
    private var firebaseMessaging: FirebaseMessaging,
    private var context: Context
) {


    private val postLiveData = MutableLiveData<Resource<Boolean>>()
    suspend fun uploadPost(post: Post) : MutableLiveData<Resource<Boolean>>{
        val timeStamp = System.currentTimeMillis().toString()
        val filePathAndName = "Posts/Post_$timeStamp"
        post.postTime = timeStamp
        post.postId=timeStamp
        if (post.postType == "image" || post.postType == "video") {
            val storageReference = refStorage.child(filePathAndName)
            storageReference.putFile(Uri.parse(post.postAttachment))
                .addOnSuccessListener { taskSnapshot ->
                    val uriTask = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val downloadUri = uriTask.result.toString()
                    if (uriTask.isSuccessful) {
                        post.postAttachment = downloadUri
                        val ref = refDatabase.child(Constants.POSTS)
                        ref.child(timeStamp).setValue(post).addOnSuccessListener {
                            postLiveData.value= Resource.success(true)
                            Toast.makeText(context, "Post Published", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { e ->
                            postLiveData.value= Resource.error(e.message.toString(),false)
                        }
                    }
                }.addOnFailureListener {
                Toast.makeText(context, "" + it.message, Toast.LENGTH_SHORT).show();
            }
        } else {
            val ref = refDatabase.child("Posts")
            ref.child(timeStamp).setValue(post).addOnSuccessListener {
                postLiveData.value= Resource.success(true)
            }.addOnFailureListener { e ->
                postLiveData.value= Resource.error(e.message.toString(),false)
            }
        }
        return postLiveData
    }



    suspend fun changeNameOrBio(value: String, key: String):MutableLiveData<Resource<Boolean>> {
        var changeNameOrBioLiveData= MutableLiveData<Resource<Boolean>>()
        auth.currentUser?.uid?.let {
            refDatabase.child(Constants.USERS).child(it).child("" + key).setValue(value)
                .addOnSuccessListener {
                    changeNameOrBioLiveData.value= Resource.success(true)

                }.addOnFailureListener{
                    changeNameOrBioLiveData.value= Resource.error(it.localizedMessage,false)

                }
        }
        return changeNameOrBioLiveData
    }


    suspend fun changePhotoOrCover(uri: Uri,keyInStorage: String,keyInDB: String):MutableLiveData<Resource<Boolean>>{
        var changePhotoOrCoverLiveData= MutableLiveData<Resource<Boolean>>()

        auth.currentUser?.uid?.let {

            refStorage.child(""+keyInStorage).child(it).putFile(uri).addOnSuccessListener {uploadTask->
                var uriTask=uploadTask.storage.downloadUrl
                while (!uriTask.isSuccessful);

                val downloadUri = uriTask.result.toString()

                if (uriTask.isSuccessful){
                    refDatabase.child(Constants.USERS).child(it).child(""+keyInDB).setValue(downloadUri)
                        .addOnSuccessListener {
                            changePhotoOrCoverLiveData.value= Resource.success(true)
                        }
                        .addOnFailureListener {
                            changePhotoOrCoverLiveData.value= Resource.error(it.localizedMessage,false)
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(context, "" + it.message, Toast.LENGTH_SHORT).show()
                changePhotoOrCoverLiveData.value= Resource.error(it.localizedMessage,false)

            }
        }
        return changePhotoOrCoverLiveData
    }

     suspend fun setLike(post: Post){


        var postLikes: Int = post.postLikes
        var mProcessLike = true
        //get id of the post clicked
        val postId: String = post.postId

         if (postId.isNotEmpty()){
             val myId= auth.currentUser?.uid
             refDatabase.child(Constants.LIKES).addValueEventListener(object : ValueEventListener {
                 override fun onDataChange(dataSnapshot: DataSnapshot) {
                     if (mProcessLike) { //already liked ,so remove  like
                         mProcessLike = if (dataSnapshot.child(postId).hasChild(myId!!)) {

                             refDatabase.child(Constants.POSTS).child(postId).child(Constants.POSTLIKES).setValue(( -- postLikes))
                             refDatabase.child(Constants.LIKES).child(postId).child(myId).removeValue()
                             false
                         } else { //not liked , liked it
                             refDatabase.child(Constants.POSTS).child(postId).child(Constants.POSTLIKES).setValue(( ++ postLikes ))
                             refDatabase.child(Constants.LIKES).child(postId).child(myId).setValue("Liked")
                             false
                         }
                     }
                 }
                 override fun onCancelled(databaseError: DatabaseError) {
                     Toast.makeText(context, "Error from likeRepo"+databaseError.message, Toast.LENGTH_SHORT).show()
                 }
             })
         }

    }



    var mProcessComment = true
   suspend fun postComment(post:Post,comment: Comment){
        //put this data in DB :
        refDatabase.child(Constants.POSTS).child(post.postId)
            .child(Constants.COMMENTS).child(comment.timeStamp)
            .setValue(comment).addOnSuccessListener { // added
            Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show()
            mProcessComment = true
            updateCommentCount(postId = post.postId)
        }.addOnFailureListener { e -> //failed
            Toast.makeText(context, "" + e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun updateCommentCount(postId:String) {
        //whenever user adds comments increase the comments counts as we did for like count
        val ref = refDatabase.child(Constants.POSTS).child(postId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mProcessComment) {
                    val comments = ( dataSnapshot.child("postComments").value ?: "0").toString()
                    val newCommentVal = comments.toInt() + 1
                    ref.child("postComments").setValue(newCommentVal)
                    mProcessComment = false
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }



    fun uploadToken(): MutableLiveData<Resource<String>> {
        val mLiveData= MutableLiveData<Resource<String>>()
        firebaseMessaging.token.addOnSuccessListener { token ->
            val uid = auth.currentUser?.uid.toString()
            refDatabase.child(Constants.USERS)
                .child(uid)
                .child("token")
                .setValue(token.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mLiveData.value= Resource.success(token)
                    }else{
                        mLiveData.value= Resource.error(task.exception?.message.toString(),null)
                    }
                }
        }
        return mLiveData
    }

    fun updateUserStatus(status: String){
        val uid = auth.currentUser?.uid.toString()
        val ref= refDatabase.child("${Constants.USERS}/$uid/status")
        val connectedRef= refDatabase.child(".info/connected")

        connectedRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected=snapshot.getValue(Boolean::class.java)
                if(connected == true){
                    ref.onDisconnect().setValue("offline")
                    ref.setValue("online")
                }else{
                    ref.setValue("offline")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        val mp= mutableMapOf<String, Any>()
        mp["status"] = status
        //ref.updateChildren(mp)
        ref.onDisconnect().setValue("offline")
        ref.setValue("online")
    }

    private val uid = auth.currentUser?.uid.toString()

    private val currentUserLiveData=MutableLiveData<Resource<User>>()
    suspend fun getCurrentUserData(): MutableLiveData<Resource<User>> {
        refDatabase.child(Constants.USERS)
            .child(uid)
            .get().addOnSuccessListener { snapShot->
                val user=snapShot.getValue(User::class.java)
                currentUserLiveData.value= Resource.success(user)
            }.addOnFailureListener {
                currentUserLiveData.value= Resource.error(it.message.toString(),null)
            }
        return currentUserLiveData
    }

    private val videoOnlyLiveData=MutableLiveData<Resource<List<Post>>>()
    suspend fun getVideosOnly(): MutableLiveData<Resource<List<Post>>> {
        var postList: ArrayList<Post> = ArrayList()
        videoOnlyLiveData.value = Resource.loading(null)
        val query = refDatabase.child(Constants.POSTS).orderByChild("postType").equalTo("video")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                snapshot.children.forEach { child ->
                    val post = child.getValue<Post>()
                    postList.add(post!!)
                }
                videoOnlyLiveData.value = Resource.success(postList)
            }

            override fun onCancelled(error: DatabaseError) {
                videoOnlyLiveData.value = Resource.error(error.message, null)

            }
        })
        return videoOnlyLiveData
    }

    private val postsLiveData=MutableLiveData<Resource<List<Post>>>()
    suspend   fun getPosts(): MutableLiveData<Resource<List<Post>>> {
        postsLiveData.value = Resource.loading(null)

        try {
            var postList: ArrayList<Post> = ArrayList()
            refDatabase.child(Constants.POSTS)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        postList.clear()
                        snapshot.children.forEach { child ->
                            val post = child.getValue<Post>()
                            postList.add(post!!)
                        }
                        postsLiveData.value = Resource.success(postList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        postsLiveData.value = Resource.error(error.message, null)

                    }
                })
        }catch (e:Exception) {
            postsLiveData.value = Resource.error(e.message.toString(), null)
        }


        return postsLiveData

    }

    private val postsForSpecificUserLiveData=MutableLiveData<Resource<List<Post>>>()
    suspend fun getPostsForSpecificUser(userID:String): MutableLiveData<Resource<List<Post>>> {
        var postList: ArrayList<Post> = ArrayList()
        postsForSpecificUserLiveData.value = Resource.loading(null)
        var query=refDatabase.child(Constants.POSTS).orderByChild("userId").equalTo(userID)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                snapshot.children.forEach { child ->
                    val post = child.getValue<Post>()
                    postList.add(post!!)
                }
                postsForSpecificUserLiveData.value = Resource.success(postList)
            }

            override fun onCancelled(error: DatabaseError) {
                postsForSpecificUserLiveData.value = Resource.error(error.message, null)

            }
        })
        return postsForSpecificUserLiveData
    }

    private val commentsLiveData=MutableLiveData<Resource<List<Comment>>>()
    suspend  fun loadComments(postId:String): MutableLiveData<Resource<List<Comment>>> {
        var commentsList: ArrayList<Comment> = ArrayList()
        commentsLiveData.value = Resource.loading(null)
        refDatabase.child(Constants.POSTS).child(postId).child("Comments")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    commentsList.clear()
                    snapshot.children.forEach { child ->
                        val comment = child.getValue<Comment>()
                        commentsList.add(comment!!)
                    }
                    commentsLiveData.value = Resource.success(commentsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    commentsLiveData.value = Resource.error(error.message, null)

                }
            })
        return commentsLiveData
    }

    private val specificUserLiveData=MutableLiveData<Resource<User>>()
    suspend  fun getSpecificUserData(userID: String ): MutableLiveData<Resource<User>> {
        refDatabase.child(Constants.USERS)
            .child(userID)
            .get().addOnSuccessListener { snapShot->
                val user=snapShot.getValue(User::class.java)
                specificUserLiveData.value= Resource.success(user)
            }.addOnFailureListener {
                specificUserLiveData.value= Resource.error(it.message.toString(),null)
            }
        return specificUserLiveData
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



/*    val myLanguage = Locale.getDefault().language
    private val translateLiveData=MutableLiveData<Resource<String>>()
    suspend fun translate(text: String,sourceLanguage: String):MutableLiveData<Resource<String>>{
        // Create an sourceLanguage-myLanguage translator:
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(myLanguage)
            .build()

        val translator = Translation.getClient(options)
        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
                //translate
                translator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        // Translation successful.
                        targetLanguage = translatedText
                        holder.itemView.apply {
                            post_caption.text=translatedText
                            post_language.visibility= View.VISIBLE
                            post_language.text="translated  from $sourceLanguage ${context.getString(
                                R.string.see_original)}"
                            post_language.paintFlags = post_language.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG

                            post_language.setOnClickListener {
                                post_caption.text=text
                                post_language.visibility= View.GONE

                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Error.
                        // ...
                    }

            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                // ...
            }






        return translateLiveData
    }*/

}

