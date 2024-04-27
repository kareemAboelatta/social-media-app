package com.example.socialmediaapp.ui.messenger.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.adapter.ConversationAdapter
import com.example.socialmediaapp.databinding.ConversationToolbarBinding
import com.example.socialmediaapp.databinding.FragmentConversationBinding
import com.example.socialmediaapp.models.User
import com.example.socialmediaapp.ui.messenger.ViewModelMessenger
import com.example.common.ui.utils.Status
import dagger.hilt.android.AndroidEntryPoint

import javax.inject.Inject


@AndroidEntryPoint
class ConversationFragment: Fragment() {

    lateinit var user: User
    private val args: ConversationFragmentArgs by navArgs()


    private val viewModel by viewModels<ViewModelMessenger>()

    private lateinit var adapter: ConversationAdapter


    @Inject
    lateinit var glide: RequestManager

    private var _binding: FragmentConversationBinding? = null
    private var bindingToolbarBinding: ConversationToolbarBinding? = null
    private val binding get() = _binding!!




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = args.user

        Log.d(TAG, "onViewCreated: args.user=== ${user} ")



        activity?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.GONE
        setHasOptionsMenu(true)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentConversationBinding.inflate(inflater, container, false)
        bindingToolbarBinding = _binding!!.appBar
        return binding.root
    }

    private  val TAG = "ConversationFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.messageEdittext.requestFocus()


        setupUI()
        setupRecycler()
        loadChat()
        sendMessage()
    }


    private fun sendMessage() {
        binding.sendMsgBtn.setOnClickListener {
            val messageBody = binding.messageEdittext.text.toString()
            if (messageBody.isNotEmpty()) {
                viewModel.sendMessage(user, messageBody).observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> binding.messageEdittext.setText("")
                        Status.ERROR -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }
        }
    }

    private fun loadChat() {
        viewModel.loadChat(user.id).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    adapter.messages = it.data!!
                    binding.conversationRv.scrollToPosition(adapter.itemCount - 1)


                }
                Status.ERROR -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    private fun setupRecycler() {
        adapter = ConversationAdapter(emptyList(), requireContext())
        binding.conversationRv.layoutManager = LinearLayoutManager(context)
        binding.conversationRv.adapter = adapter
    }



    private fun setupUI() {

        (activity as AppCompatActivity).setSupportActionBar(bindingToolbarBinding?.customToolbar)


        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }
        bindingToolbarBinding?.userName?.text=user.name
        bindingToolbarBinding?.let {
            glide.load(user.image)
                .placeholder(R.drawable.default_user)
                .into(it.userImage)
        }

    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bindingToolbarBinding = null
    }
}