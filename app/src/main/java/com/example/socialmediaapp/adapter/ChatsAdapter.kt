package com.example.socialmediaapp.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ChatLayoutBinding
import com.example.socialmediaapp.models.Chat
import com.example.socialmediaapp.models.User
import com.example.socialmediaapp.repository.Repository
import com.example.socialmediaapp.common.Utils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ChatsAdapter @Inject constructor (
    @ApplicationContext var context: Context,
    var repository: Repository,
    private val glide: RequestManager,
    private var auth: FirebaseAuth
) :  RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {



    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Chat>() {

            override fun areItemsTheSame(oldItem: Chat, newItem: Chat) =
                oldItem.userID == newItem.userID

            override fun areContentsTheSame(oldItem: Chat, newItem: Chat) =
                oldItem.userID == newItem.userID

        }
    }

    val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    inner class ChatsViewHolder(val binding: ChatLayoutBinding) : RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder =
        ChatsViewHolder(ChatLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))




    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val chat = differ.currentList[position]

        holder.binding.apply {
            chatUsername.text = chat.userName                          // set user name
            chatLastMessage.text = chat.lastMessage                    // set chat message
            //---------------
            if (!chat.seen) {
                chatUsername.setTypeface(null, Typeface.BOLD)
                chatLastMessage.setTextColor(context.getColor(R.color.black))
                chatLastMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                chatLastMessage.setTypeface(null, Typeface.BOLD)
            }
            //---------------
            Utils.getChatTime(chat.time).let {
                //set chat time
                messageTimeTv.text = it
            }
            //---------------
            if (chat.seen) {                                   // set seen notification
                unreadMessagesNotify.visibility = View.GONE
            }
            //---------------
            glide.load(chat.userImageUrl)  //set user image
                .placeholder(context?.let { it1 ->
                    AppCompatResources.getDrawable(it1, R.drawable.default_user)
                })
                .into(chatUserImage)
            //---------------

            holder.itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(
                        User(
                        id = chat.userID,
                        image = chat.userImageUrl,
                        token = chat.token,
                        name = chat.userName
                        )
                    )
                }

            }


        }

    }

    private var onItemClickListener: ((User) -> Unit)? = null
    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}