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
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.models.Chat
import com.example.socialmediaapp.models.Post
import com.example.socialmediaapp.models.User
import com.example.socialmediaapp.repository.Repository
import com.example.socialmediaapp.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.android.synthetic.main.chat_layout.view.*
import java.util.ArrayList
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

    inner class ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        return ChatsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.chat_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val chat = differ.currentList[position]

        holder.itemView.apply {
            chat_username.text = chat.userName                          // set user name
            chat_last_message.text = chat.lastMessage                    // set chat message
            //---------------
            if (!chat.seen) {
                chat_username.setTypeface(null, Typeface.BOLD)
                chat_last_message.setTextColor(context.getColor(R.color.black))
                chat_last_message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                chat_last_message.setTypeface(null, Typeface.BOLD)
            }
            //---------------
            Utils.getChatTime(chat.time).let {
                //set chat time
                message_time_tv.text = it
            }
            //---------------
            if (chat.seen) {                                   // set seen notification
                unread_messages_notify.visibility = View.GONE
            }
            //---------------
            glide.load(chat.userImageUrl)  //set user image
                .placeholder(context?.let { it1 ->
                    AppCompatResources.getDrawable(it1, R.drawable.default_user)
                })
                .into(chat_user_image)
            //---------------

            setOnClickListener {
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