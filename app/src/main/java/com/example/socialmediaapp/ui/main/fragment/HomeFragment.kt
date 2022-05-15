package com.example.socialmediaapp.ui.main.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.ui.main.ViewModelMain
import com.example.socialmediaapp.adapter.AdapterPost
import com.example.socialmediaapp.models.Post
import com.example.socialmediaapp.utils.Status
import com.google.firebase.auth.FirebaseAuth
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var homeAdapter:AdapterPost
    private val viewModel by viewModels<ViewModelMain>()
    private var postList : ArrayList<Post> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewSetUp()
        viewModel.getPosts()

        viewModel.postsLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    home_progress_bar?.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    home_progress_bar.visibility = View.GONE
                    postList = it.data as ArrayList<Post>
                    homeAdapter.setList(postList)
                }
                Status.ERROR -> {
                    home_progress_bar?.visibility = View.GONE
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

    private fun recyclerViewSetUp(){
        val linearLayout = LinearLayoutManager(activity)
        linearLayout.stackFromEnd = true
        linearLayout.reverseLayout = true
        home_rec.layoutManager=linearLayout
        homeAdapter.setList(postList)
        home_rec.adapter=homeAdapter

    }
}