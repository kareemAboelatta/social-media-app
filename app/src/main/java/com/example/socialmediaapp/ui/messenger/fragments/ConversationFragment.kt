package com.example.socialmediaapp.ui.messenger.fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.adapter.ConversationAdapter
import com.example.socialmediaapp.models.User
import com.example.socialmediaapp.ui.messenger.ViewModelMessenger
import com.example.socialmediaapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_messenger.*
import kotlinx.android.synthetic.main.conversation_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_conversation.*
import javax.inject.Inject


@AndroidEntryPoint
class ConversationFragment: Fragment(R.layout.fragment_conversation) {

    lateinit var user: User
    private val args: ConversationFragmentArgs by navArgs()


    private val viewModel by viewModels<ViewModelMessenger>()

    private lateinit var adapter: ConversationAdapter


    @Inject
    lateinit var glide: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user=args.user
        message_edittext.requestFocus()




        setupUI()
        setupRecycler()
        loadChat()
        sendMessage()


    }

    private fun sendMessage() {
        send_msg_btn.setOnClickListener {
            val messageBody = message_edittext.text.toString()
            if (messageBody.isNotEmpty()) {
                viewModel.sendMessage(user, messageBody).observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            message_edittext.setText("")
                        }
                        Status.ERROR -> {
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }
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
                    conversation_rv?.scrollToPosition(adapter.itemCount - 1)
                }
                Status.ERROR -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecycler() {
        adapter = ConversationAdapter(emptyList(),requireContext())
        conversation_rv.layoutManager = LinearLayoutManager(context)
        conversation_rv.adapter = adapter
    }
    private fun setupUI() {

        (activity as AppCompatActivity).setSupportActionBar(app_bar.custom_toolbar)


        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }
        app_bar.user_name.text=user.name
        glide.load(user.image)
            .placeholder(R.drawable.default_user)
            .into(app_bar.user_image)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.bottom_navigation?.visibility = View.GONE
        setHasOptionsMenu(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            requireActivity().onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.bottom_navigation?.visibility = View.VISIBLE
    }
}