package com.example.socialmediaapp.ui.messenger.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.adapter.AdapterPeople
import com.example.socialmediaapp.adapter.ChatsAdapter
import com.example.socialmediaapp.models.Chat
import com.example.socialmediaapp.models.User
import com.example.socialmediaapp.ui.messenger.ViewModelMessenger
import com.example.socialmediaapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chats.*
import kotlinx.android.synthetic.main.fragment_people.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : Fragment(R.layout.fragment_chats) {

    private val viewModel by viewModels<ViewModelMessenger>()



    @Inject
    lateinit var adapter: ChatsAdapter


    @Inject
    lateinit var glide: RequestManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayout = LinearLayoutManager(activity)
        chats_rv.layoutManager=linearLayout



        getChats()
        setupUI()
        getCurrentUser()
    }



    private fun setupUI() {

        val img=searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        img.setColorFilter(R.color.purple_200)
        chats_rv.layoutManager= LinearLayoutManager(context)
        chats_rv.adapter=adapter
    }

    private fun getChats() {

        viewModel.loadChats().observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    chats_progressbar?.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    chats_progressbar?.visibility = View.GONE
                    adapter.differ.submitList(it.data)
                }
                Status.ERROR -> {
                    chats_progressbar?.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        adapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("user", it)
            }
            findNavController().navigate(
                R.id.action_chatsFragment_to_conversationFragment,
                bundle
            )

        }

    }

    private fun getCurrentUser(){
        viewModel.currentUser.observe(requireActivity()) {
            glide.load(it.image)
                .placeholder(context?.let { it1 ->
                    AppCompatResources.getDrawable(
                        it1,
                        R.drawable.default_user
                    )
                })
                .into(user_image)
        }
    }


}