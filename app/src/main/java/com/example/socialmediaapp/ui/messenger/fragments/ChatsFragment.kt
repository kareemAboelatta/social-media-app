package com.example.socialmediaapp.ui.messenger.fragments

import android.os.Bundle
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.adapter.ChatsAdapter
import com.example.socialmediaapp.databinding.FragmentChatsBinding
import com.example.socialmediaapp.ui.messenger.ViewModelMessenger
import com.example.common.ui.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ViewModelMessenger>()

    @Inject
    lateinit var adapter: ChatsAdapter

    @Inject
    lateinit var glide: RequestManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        getChats()
        getCurrentUser()
    }

    private fun setupUI() {
        val img = binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        img.setColorFilter(resources.getColor(R.color.purple_200, null))
        binding.chatsRv.layoutManager = LinearLayoutManager(context)
        binding.chatsRv.adapter = adapter
    }

    private fun getChats() {
        viewModel.loadChats().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.chatsProgressbar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.chatsProgressbar.visibility = View.GONE
                    adapter.differ.submitList(it.data)
                }
                Status.ERROR -> {
                    binding.chatsProgressbar.visibility = View.GONE
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

    private fun getCurrentUser() {
        viewModel.currentUser.observe(viewLifecycleOwner) {
            glide.load(it.image)
                .placeholder(AppCompatResources.getDrawable(requireContext(), R.drawable.default_user))
                .into(binding.userImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
