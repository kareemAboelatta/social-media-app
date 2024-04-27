package com.example.socialmediaapp.ui.main.fragment

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
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
import com.example.socialmediaapp.databinding.FragmentPostDetailsBinding
import com.example.socialmediaapp.models.Comment
import com.example.socialmediaapp.models.Post
import com.example.common.ui.utils.Status
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
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import dagger.hilt.android.AndroidEntryPoint

import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class PostDetailsFragment :  Fragment() {

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

    private var _binding: FragmentPostDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        commentAdapter = AdapterComment(
            requireContext(),
            glide = glide,
            myUserId = auth.currentUser?.uid ?: "",
            onDelete = ::deleteComment

        )
        commentAdapter.context = requireContext() // we need context of activity to we can show dialog.
    }


    private fun deleteComment(comment: Comment) {
        val ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId)
        ref.child("Comments").child(comment.commentId).removeValue() // it will delete the comment

        //now update the comment count
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val comments = "" + dataSnapshot.child("postComments").value
                val newCommentVal = comments.toInt() - 1
                ref.child("postComments").setValue(newCommentVal)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


        binding.detPostCommentTV.text= (binding.detPostCommentTV.text.toString().toInt()-1).toString()

        Toast.makeText(context, "Comment deleted..", Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<ChipNavigationBar>(R.id.bottom_menu)?.visibility = View.GONE

        post = args.post
        hisUserId = args.post.userId.toString()
        postId = args.post.postId

        t1 = TextToSpeech(activity) { status ->
            if (status != TextToSpeech.ERROR) {
                t1!!.language = Locale.ENGLISH
            }
        }

        // User data
        postLikes = post.postLikes
        postImage = post.postAttachment
        binding.detUNameIv.text = post.userName
        commentAdapter.postId = post.postId
        glide.load(post.userImage).into(binding.detUserPictureIv)

        binding.detUNameIv.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("post", post)
            }
            findNavController().navigate(
                R.id.action_postDetailsFragment_to_postOwnerFragment,
                bundle
            )
        }

        // Post data
        val cal = Calendar.getInstance(Locale.getDefault())
        cal.timeInMillis = post.postTime.toLong()
        val time = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString()
        binding.detPTimeIv.text = time
        binding.detPTitleIv.text = post.caption
        binding.detPostLikesTV.text = post.postLikes.toString()
        binding.detPostCommentTV.text = post.postComments.toString()

        // Setup based on post type
        when (post.postType) {
            "article" -> {
                binding.detPImageIv.visibility = View.GONE
                binding.detVideo.visibility = View.GONE
            }
            "image" -> {
                binding.detVideo.visibility = View.GONE
                binding.detPImageIv.visibility = View.VISIBLE
                glide.load(post.postAttachment).into(binding.detPImageIv)
            }
            "video" -> {
                binding.detPImageIv.visibility = View.GONE
                binding.detVideo.visibility = View.VISIBLE

                val simpleExoPlayer = SimpleExoPlayer.Builder(mycontext).build()
                val videoUri = Uri.parse(post.postAttachment)
                val mediaSource = buildMediaSource(videoUri)
                simpleExoPlayer.prepare(mediaSource)
                simpleExoPlayer.playWhenReady = false
                binding.detVideo.player = simpleExoPlayer
            }
        }

        binding.detPostLikeBtn.setOnClickListener {
            viewModel.setLike(post)
        }

        binding.detBtnComment.setOnClickListener {
            postComment()
        }

        setLikes()
        loadComments()

        binding.detMoreBtn.setOnClickListener {
            showMoreOptions(binding.detMoreBtn, hisUserId, postId, postImage ?: "")
        }

        binding.detPostReadBtn.setOnClickListener {
            t1?.speak(post.caption, TextToSpeech.QUEUE_FLUSH, null)
        }

        // Fetching data for this user
        viewModel.getDataForCurrentUser()
        viewModel.currentUserLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    myName = it.data?.name
                    myImage = it.data?.image
                    glide.load(myImage).error(R.drawable.ic_profile).into(binding.detCAvatarTv)
                }
                Status.ERROR -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    private fun loadComments() {
        val layoutManager = LinearLayoutManager(activity)
        binding.detRecComments.layoutManager = layoutManager

        viewModel.loadComments(post.postId)
        viewModel.commentsLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.detProgressBarComments.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.detProgressBarComments.visibility = View.GONE
                    commentAdapter.differ.submitList(it.data)
                    binding.detRecComments.adapter = commentAdapter
                }
                Status.ERROR -> {
                    binding.detProgressBarComments.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(mycontext, "exoPlayer")
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
    }


    private fun setLikes() {
        refDatabase.child("Likes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (imVisiable) {
                    if (dataSnapshot.child(postId).hasChild(auth.currentUser?.uid!!)) {
                        //user has liked for this post
                       binding.detPostLikeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_like, 0, 0, 0
                        )
                        binding.detPostLikeBtn.setText("Liked")
                        binding.detPostLikesTV.text=""+(binding.detPostLikesTV.text.toString().toInt()+1)

                    } else {
                        //user has not liked for this post
                        binding.detPostLikeBtn.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_like_not, 0, 0, 0
                        )
                        binding.detPostLikesTV.text=""+(binding.detPostLikesTV.text.toString().toInt()-1)

                        binding.detPostLikeBtn.setText("Like")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }
    private fun postComment() {

        //get data from comment edit text
        val comment= binding.detCommentEt.text.toString()
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
        binding.detCommentEt.setText("")
        binding.detPostCommentTV.text=""+(binding.detPostCommentTV.text.toString().toInt()+1)
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
        _binding = null
        activity?.findViewById<ChipNavigationBar>(R.id.bottom_menu)?.visibility = View.VISIBLE
    }




    override fun onPause() {
        super.onPause()
        t1?.stop()
        t1?.shutdown()
        imVisiable=false
    }

}