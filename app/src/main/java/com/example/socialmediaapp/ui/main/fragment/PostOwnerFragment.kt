package com.example.socialmediaapp.ui.main.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.ui.main.ViewModelMain
import com.example.socialmediaapp.adapter.AdapterPost
import com.example.socialmediaapp.models.Post
import com.example.socialmediaapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_post_owner.*
import javax.inject.Inject

@AndroidEntryPoint
class PostOwnerFragment : Fragment(R.layout.fragment_post_owner) {
    @Inject
    lateinit var glide: RequestManager


    @Inject
    lateinit var adapterPosts: AdapterPost

    private val viewModel by viewModels<ViewModelMain>()

    lateinit var postList : ArrayList<Post>
    lateinit var prog: ProgressDialog


    val args:PostOwnerFragmentArgs by navArgs()
    lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postList= ArrayList()
        prog= ProgressDialog(activity)
        prog.setMessage("Wait..")
        prog.setCancelable(false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.bottom_menu?.visibility = View.GONE

        post=args.post

        recyclerViewSetUp()

        viewModel.getPostsForSpecificUser(post.userId!!)
        viewModel.getSpecificUserData(post.userId!!)

        //observe Posts from viewModel
        viewModel.postsForSpecificUserLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    prof_ProgressBar?.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    prof_ProgressBar.visibility = View.GONE
                    postList = it.data as ArrayList<Post>
                    adapterPosts.setList(postList)
                }
                Status.ERROR -> {
                    prof_ProgressBar?.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }

        }


        adapterPosts.setonItemClickListenerForLike {
            viewModel.setLike(it)
        }
        //observe data for this user from viewModel
        viewModel.specificUserLiveData.observe(viewLifecycleOwner) {
            when(it.status){
                Status.SUCCESS->{
                    val user=it.data
                    prof_name.text = user?.name
                    prof_bio.text = user?.bio


                    glide.load(user?.image).error(R.drawable.ic_profile).into(prof_image_profile)


                    glide.load(user?.cover).error(R.drawable.ic_image_default).into(prof_image_cover)
                    prof_image_cover.scaleType = ImageView.ScaleType.CENTER_CROP
                }

                Status.ERROR ->{

                }

            }



            prog.dismiss()
        }


    }

    fun recyclerViewSetUp(){
        //put linearLayout in recycle
        val linearLayout = LinearLayoutManager(activity)
        linearLayout.stackFromEnd = true
        linearLayout.reverseLayout = true
        prof_rec.layoutManager=linearLayout
        adapterPosts.setList(postList)
        prof_rec.adapter=adapterPosts
    }

    override fun onDestroy() {
        super.onDestroy()

        activity?.bottom_menu?.visibility = View.VISIBLE

    }

}