package com.example.socialmediaapp.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.ui.main.ViewModelMain
import com.example.socialmediaapp.adapter.AdapterPost
import com.example.socialmediaapp.models.Post
import com.example.common.ui.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import com.example.socialmediaapp.databinding.FragmentVideoBinding
import javax.inject.Inject

@AndroidEntryPoint
class VideosFragment  : Fragment() {

    @Inject
    lateinit var videoAdapter: AdapterPost

    private val viewModel by viewModels<ViewModelMain>()

    lateinit var postList : ArrayList<Post>

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postList = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerSetup()

        viewModel.getVideosOnly()

        viewModel.videoOnlyLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.videoProgressBar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.videoProgressBar.visibility = View.GONE
                    postList = it.data as ArrayList<Post>
                    videoAdapter.setList(postList)
                }
                Status.ERROR -> {
                    binding.videoProgressBar.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        videoAdapter.setonItemClickListenerForLike {
            viewModel.setLike(it)
        }

        videoAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("post", it)
            }
            findNavController().navigate(
                R.id.action_videosFragment_to_postDetailsFragment,
                bundle
            )
        }

        videoAdapter.setOnItemClickListenerForGoingtoOwner {
            val bundle = Bundle().apply {
                putSerializable("post", it)
            }
            findNavController().navigate(
                R.id.action_videosFragment_to_postOwnerFragment,
                bundle
            )
        }
    }

    private fun recyclerSetup(){
        val linearLayout = LinearLayoutManager(activity)
        linearLayout.stackFromEnd = true
        linearLayout.reverseLayout = true
        binding.videoRec.layoutManager = linearLayout
        videoAdapter.setList(postList)
        binding.videoRec.adapter = videoAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}