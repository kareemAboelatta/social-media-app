package com.example.socialmediaapp.ui.main.fragment

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.ui.main.ViewModelMain
import com.example.socialmediaapp.adapter.AdapterComment
import com.example.socialmediaapp.models.Comment
import com.example.socialmediaapp.models.Post
import com.example.socialmediaapp.utils.Status
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_post_details.*
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class PostDetailsFragment :  Fragment(R.layout.fragment_post_details) {

    @Inject
    lateinit var glide:RequestManager

    @Inject
    lateinit var refDatabase: DatabaseReference

    @Inject
    lateinit var refStorage: StorageReference

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var mycontext: Context

    @Inject
    lateinit var commentAdapter: AdapterComment


    var imVisiable=true


    var postLikes : Int =0


    val args:PostDetailsFragmentArgs by navArgs()


    var postImage: String? = null

    var postId:String ="args.post.postId"


    var myName: String? = null
    var myImage: String? = null
    var hisUserId: String ="args.post.postId"


    var t1: TextToSpeech? = null

    private val viewModel by viewModels<ViewModelMain>()

    lateinit var post:Post

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.bottom_menu?.visibility = View.GONE

        post=args.post

        hisUserId=args.post.postId
        postId=args.post.postId

        t1 = TextToSpeech(activity) { status ->
            if (status != TextToSpeech.ERROR) {
                t1!!.language = Locale.ENGLISH
            }
        }


        //user data
        postLikes=post.postLikes
        postImage = post.postAttachment
        det_uNameIv.text=post.userName
        commentAdapter.postId=post.postId
        glide.load(post.userImage).into(det_userPictureIv)

        det_uNameIv.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("post", post)
            }
            findNavController().navigate(
                R.id.action_postDetailsFragment_to_postOwnerFragment,
                bundle
            )
        }
        //post data
        //get time from timestamp
        val cal = Calendar.getInstance(Locale.getDefault())
        cal.timeInMillis = post.postTime.toLong()

        val time = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString()
        det_pTimeIv.text=time
        det_pTitleIv.text=post.caption
        det_post_LikesTV.text=post.postLikes.toString()
        det_post_CommentTV.text=post.postComments.toString()


        when (post.postType) {
            "article" -> {
                det_pImageIv.visibility=View.GONE
                det_video.visibility=View.GONE
            }
            "image" -> {
                det_video.visibility=View.GONE
                det_pImageIv.visibility=View.VISIBLE
                glide.load(post.postAttachment).into(det_pImageIv)
            }
            "video" -> {
                det_pImageIv.visibility=View.GONE
                det_video.visibility=View.VISIBLE


                var simpleExoPlayer: SimpleExoPlayer = SimpleExoPlayer.Builder(mycontext).build()
                val video: Uri = Uri.parse(post.postAttachment)
                val mediaSource: MediaSource =buildMediaSource(video)
                simpleExoPlayer.prepare(mediaSource)
                simpleExoPlayer.playWhenReady =false
                det_video.player=simpleExoPlayer


            }
        }

        det_post_like_btn.setOnClickListener {
            viewModel.setLike(post)
        }


        det_btn_comment.setOnClickListener {
            postComment()
        }

        setLikes()

        loadComments()

        det_more_btn.setOnClickListener {
            showMoreOptions(det_more_btn, hisUserId!!, postId!!, postImage!!)
        }

        det_post_read_btn.setOnClickListener {
            t1?.speak(post.caption, TextToSpeech.QUEUE_FLUSH, null)
        }





        viewModel.getDataForCurrentUser()
        //data for this user
        viewModel.currentUserLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    myName = "" + it.data?.name
                    myImage = "" + it.data?.image
                    glide.load(myImage).error(R.drawable.ic_profile).into(det_cAvatarTv)

                }
                Status.ERROR -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }

        }


    }


    private fun loadComments() {
        //layout (linear) for recycleview

        val layoutManager = LinearLayoutManager(activity)
        det_rec_comments.layoutManager=layoutManager

        viewModel.loadComments(post.postId)
        viewModel.commentsLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    det_ProgressBar_comments?.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    det_ProgressBar_comments.visibility = View.GONE
                    commentAdapter.differ.submitList(it.data)
                    det_rec_comments.adapter = commentAdapter
                }
                Status.ERROR -> {
                    det_ProgressBar_comments?.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }

        }

    }
    private fun buildMediaSource (uri: Uri) : MediaSource{
        val dataSourceFactory : DataSource.Factory = DefaultDataSourceFactory(mycontext,"exoPlayer")
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource((MediaItem.fromUri(uri)))
    }
    private fun setLikes() {
        refDatabase.child("Likes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (imVisiable) {
                    if (dataSnapshot.child(postId!!).hasChild(auth.currentUser?.uid!!)) {
                        //user has liked for this post
                        det_post_like_btn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_like, 0, 0, 0
                        )
                        det_post_like_btn.setText("Liked")
                    } else {
                        //user has not liked for this post
                        det_post_like_btn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_like_not, 0, 0, 0
                        )
                        det_post_like_btn.setText("Like")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }
    private fun postComment() {

        //get data from comment edit text
        val comment= det_commentEt.text.toString()
        //validate
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(activity, "Comment is Empty...", Toast.LENGTH_SHORT).show()
            return
        }
        val timeStamp = System.currentTimeMillis().toString()
        //each post will have a child "Comments " tha will conten comments of the post

        val myComment=
            Comment(timeStamp, comment, timeStamp,
                auth.currentUser?.uid!!, myImage!!, myName!!)

        //put this data in DB :
        viewModel.postComment(post,myComment)
        det_commentEt.setText("")
        det_post_CommentTV.text=""+(det_post_CommentTV.text.toString().toInt()+1)
    }



    private fun showMoreOptions(moreBtn: ImageButton, uid: String, pId: String, pImage: String) {
        val popupMenu = PopupMenu(activity, moreBtn, Gravity.END)
        if (hisUserId == auth.currentUser?.uid) {
            popupMenu.menu.add(Menu.NONE, 0, 0, "Delete")
        }
        popupMenu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == 0) {
                //delete
                beginDelete(pId,pImage)
            }
            false
        }
        popupMenu.show()
    }
    private fun beginDelete(pId: String, pImage: String) {
        if (pImage == "noImage") {
            deleteWithoutImage(pId)
        } else {
            deleteWithImage(pId, pImage)
        }
    }
    private fun deleteWithoutImage(pId: String) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Deleting...")
        val fquery =refDatabase.child("Posts").orderByChild("postId").equalTo(
            postId
        )
        fquery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    dataSnapshot1.ref.removeValue() // remove values from firebase where pid matches
                    progressDialog.dismiss()
                    Toast.makeText(activity, "Deleted Successfully", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, "Deleted Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun deleteWithImage(pId: String, pImage: String) {
        // Progress bar
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Deleting...")



        val picStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pImage)
        picStorageReference.delete().addOnSuccessListener {
            // image deleted ,not delete from database
            val fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postId").equalTo(
                pId
            )
            fquery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (dataSnapshot1 in dataSnapshot.children) {
                        dataSnapshot1.ref.removeValue() // remove values from firebase where pid matches
                        progressDialog.dismiss()
                        Toast.makeText(
                            activity,
                            "Deleted Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(activity, "Deleted Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }.addOnFailureListener { e -> //failed
            progressDialog.dismiss()
            Toast.makeText(activity, "" + e.message, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        t1?.stop()
        t1?.shutdown()
        imVisiable=false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        imVisiable=false
        activity?.bottom_menu?.visibility = View.VISIBLE

    }

    override fun onPause() {
        super.onPause()
        t1?.stop()
        t1?.shutdown()
        imVisiable=false
    }

}