package com.example.socialmediaapp.ui.main.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.common.ui.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


import com.example.socialmediaapp.databinding.FragmentPostOwnerBinding
import com.ismaeldivita.chipnavigation.ChipNavigationBar

@AndroidEntryPoint
class PostOwnerFragment : Fragment() {
    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var adapterPosts: AdapterPost

    private val viewModel by viewModels<ViewModelMain>()

    lateinit var postList: ArrayList<Post>
    lateinit var prog: ProgressDialog

    private var _binding: FragmentPostOwnerBinding? = null
    private val binding get() = _binding!!

    val args: PostOwnerFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postList = ArrayList()
        prog = ProgressDialog(activity)
        prog.setMessage("Wait..")
        prog.setCancelable(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostOwnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<ChipNavigationBar>(R.id.bottom_menu)?.visibility = View.GONE

        val post = args.post
        recyclerViewSetUp()

        viewModel.getPostsForSpecificUser(post.userId!!)
        viewModel.getSpecificUserData(post.userId!!)

        // Observe Posts from viewModel
        viewModel.postsForSpecificUserLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.profProgressBar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.profProgressBar.visibility = View.GONE
                    postList = it.data as ArrayList<Post>
                    adapterPosts.setList(postList)
                }
                Status.ERROR -> {
                    binding.profProgressBar.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        adapterPosts.setonItemClickListenerForLike {
            viewModel.setLike(it)
        }

        // Observe data for this user from viewModel
        viewModel.specificUserLiveData.observe(viewLifecycleOwner) {
            when(it.status){
                Status.SUCCESS -> {
                    val user = it.data
                    binding.profName.text = user?.name
                    binding.profBio.text = user?.bio
                    glide.load(user?.image).error(R.drawable.ic_profile).into(binding.profImageProfile)
                    glide.load(user?.cover).error(R.drawable.ic_image_default).into(binding.profImageCover)
                    binding.profImageCover.scaleType = ImageView.ScaleType.CENTER_CROP
                }
                Status.ERROR -> {
                    // Handle the error case
                }

                else -> {}
            }
            prog.dismiss()
        }
    }

    private fun recyclerViewSetUp(){
        val linearLayout = LinearLayoutManager(activity)
        linearLayout.stackFromEnd = true
        linearLayout.reverseLayout = true
        binding.profRec.layoutManager = linearLayout
        adapterPosts.setList(postList)
        binding.profRec.adapter = adapterPosts
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.findViewById<ChipNavigationBar>(R.id.bottom_menu)?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}