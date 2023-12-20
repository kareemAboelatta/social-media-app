package com.example.socialmediaapp.adapter

import android.content.Context
import android.graphics.Paint
import android.net.Uri
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ItemPostBinding
import com.example.socialmediaapp.models.Post
import com.example.socialmediaapp.repository.Repository
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.qualifiers.ApplicationContext

import java.util.*
import javax.inject.Inject

class AdapterPost @Inject constructor (
    @ApplicationContext var context: Context,
    var repository: Repository,
    private val glide: RequestManager,
    private var auth: FirebaseAuth
) :  RecyclerView.Adapter<AdapterPost.PostViewHolder>(){

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)


    var posts: List<Post> = ArrayList()
    val myLanguage = Locale.getDefault().language

    var myUid: String? = null
    init {
        myUid = auth.currentUser?.uid
    }

    fun setList(posts: List<Post>){
        this.posts=posts
         notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        PostViewHolder(ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun getItemCount(): Int {
        return posts.size
    }


    override fun onBindViewHolder(holder:   PostViewHolder, position: Int) {
        val   post = posts[position]
        holder.binding.apply {

            //user data
            postUserName.text=post.userName
            glide.load(post.userImage).into(postUserPicture)
            //post data
            //get time from timestamp
            val cal = Calendar.getInstance(Locale.getDefault())
            cal.timeInMillis = if(post.postTime.isNotEmpty())  post.postTime.toLong() else 0

            val time = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString()
            postTimeIv.text=time
            postCaption.text=post.caption
            postLikesTV.text=post.postLikes.toString()
            postCommentTV.text=post.postComments.toString()
            postTextAnyone.text=post.postFans
            when(post.postFans){
                "Anyone"->{
                    postImageAnyone.setImageResource(R.drawable.ic_public);
                }
                "Friends"->{
                    postImageAnyone.setImageResource(R.drawable.ic_group);
                }
                "Only me"->{
                    postImageAnyone.setImageResource(R.drawable.ic_profile);
                }
            }


            when (post.postType) {
                "article" -> {

                    postImage.visibility=View.GONE
                    postVideo.visibility=View.GONE
                }
                "image" -> {
                    postVideo.visibility=View.GONE
                    postImage.visibility=View.VISIBLE
                    glide.load(post.postAttachment).into(postImage)
                }
                "video" -> {
                    postImage.visibility=View.GONE
                    postVideo.visibility=View.VISIBLE


                    var simpleExoPlayer: SimpleExoPlayer = SimpleExoPlayer.Builder(context).build()
                    val video: Uri = Uri.parse(post.postAttachment)
                    val mediaSource: MediaSource =buildMediaSource(video)
                    simpleExoPlayer.prepare(mediaSource)
                    simpleExoPlayer.playWhenReady =false
                    postVideo.player=simpleExoPlayer


                }
            }

            postLikeBtn.setOnClickListener {
                onItemClickListenerForLike?.let { it(post) }
                notifyDataSetChanged()
            }

            setLikes(holder,post.postId)

            if (post.languageCode != myLanguage && post.languageCode != "und"){
                translate(post.caption,post.languageCode,myLanguage,holder)
            }

            postUserPicture.setOnClickListener {
                onItemClickListenerForGoingtoOwner?.let { it(post) }
            }
            postUserName.setOnClickListener {
                onItemClickListenerForGoingtoOwner?.let { it(post) }
            }

            postCommentBtn.setOnClickListener {
                onItemClickListener?.let { it(post) }
            }

            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(post) }
            }

        }

    }

    private fun buildMediaSource (uri: Uri) : MediaSource{
        val dataSourceFactory : DataSource.Factory = DefaultDataSourceFactory(context,"exoPlayer")
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource((MediaItem.fromUri(uri)))
    }


    override fun onViewDetachedFromWindow(holder: PostViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.binding.apply {
            if (postVideo.visibility == View.VISIBLE && postVideo.player?.isPlaying == true ){
                postVideo.player?.stop()
            }

        }
    }




    private var onItemClickListener: ((Post) -> Unit)? = null
    fun setOnItemClickListener(listener: (Post) -> Unit) {
        onItemClickListener = listener
    }
    private var onItemClickListenerForGoingtoOwner: ((Post) -> Unit)? = null
    fun setOnItemClickListenerForGoingtoOwner(listener: (Post) -> Unit) {
        onItemClickListenerForGoingtoOwner = listener
    }


    private var onItemClickListenerForLike: ((Post) -> Unit)? = null
    fun setonItemClickListenerForLike(listener: (Post) -> Unit) {
        onItemClickListenerForLike = listener
    }

    private fun setLikes(holder1: PostViewHolder, postKey: String) {
        repository.refDatabase.child("Likes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                holder1.binding.apply {
                    if (dataSnapshot.child(postKey).hasChild(myUid!!)) {
                        //user has liked for this post
                        postLikeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_like, 0, 0, 0)
                        postLikeBtn.text = "Liked"
                    } else {
                        //user has not liked for this post
                        postLikeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_like_not, 0, 0, 0)
                        postLikeBtn.text = "Like"
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    fun translate(text: String,sourceLanguage:String ,myLanguage:String,holder: PostViewHolder):String{
        var targetLanguage=""

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
                        holder.binding.apply {
                            postCaption.text=translatedText
                            postLanguage.visibility= View.VISIBLE
                            postLanguage.text="translated  from $sourceLanguage ${context.getString(R.string.see_original)}"
                            postLanguage.paintFlags = postLanguage.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG

                            postLanguage.setOnClickListener {
                                postCaption.text=text
                                postLanguage.visibility= View.GONE

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



        return targetLanguage
    }



}