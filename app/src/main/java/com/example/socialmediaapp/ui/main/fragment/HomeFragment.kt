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
import com.example.socialmediaapp.databinding.FragmentHomeBinding
import com.example.socialmediaapp.models.Post
import com.example.common.ui.utils.Status
import com.google.firebase.auth.FirebaseAuth
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {


    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var homeAdapter: AdapterPost
    private val viewModel by viewModels<ViewModelMain>()
    private var postList: ArrayList<Post> = ArrayList()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewSetUp()
        viewModel.getPosts()

        viewModel.postsLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.homeProgressBar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.homeProgressBar.visibility = View.GONE
                    postList = it.data as ArrayList<Post>
                    homeAdapter.setList(postList)
                }
                Status.ERROR -> {
                    binding.homeProgressBar.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        homeAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("post", it)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_postDetailsFragment,
                bundle
            )
        }
        homeAdapter.setOnItemClickListenerForGoingtoOwner {
            val bundle = Bundle().apply {
                putSerializable("post", it)
            }
            if (it.userId == auth.currentUser?.uid ){
                activity?.findViewById<ChipNavigationBar>(R.id.bottom_menu)?.setItemSelected(
                    R.id.profile)

                findNavController().navigate(R.id.profileFragment)

            }else{
                findNavController().navigate(
                    R.id.action_homeFragment_to_postOwnerFragment,
                    bundle
                )
            }

        }
        homeAdapter.setonItemClickListenerForLike {
            viewModel.setLike(it)
        }




    }

    private fun recyclerViewSetUp() {
        val linearLayout = LinearLayoutManager(activity)
        linearLayout.stackFromEnd = true
        linearLayout.reverseLayout = true
        binding.homeRec.layoutManager = linearLayout
        homeAdapter.setList(postList)
        binding.homeRec.adapter = homeAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}