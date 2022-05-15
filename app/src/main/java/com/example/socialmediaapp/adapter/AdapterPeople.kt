package com.example.socialmediaapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.models.Comment
import com.example.socialmediaapp.models.Post
import com.example.socialmediaapp.models.User
import com.example.socialmediaapp.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.user_layout.view.*
import javax.inject.Inject

class AdapterPeople @Inject constructor (
    @ApplicationContext var context: Context,
    var repository: Repository,
    private val glide: RequestManager,
    private var refDatabase: DatabaseReference,
    private var refStorage: StorageReference,
    private var auth: FirebaseAuth
) : RecyclerView.Adapter<AdapterPeople.PeopleViewHolder>(){


    inner class PeopleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {

            override fun areItemsTheSame(oldItem: User, newItem: User) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: User, newItem: User) =
                oldItem.id == newItem.id

        }
    }

    val differ = AsyncListDiffer(this, DIFF_CALLBACK)



    private var onItemClickListener: ((User) -> Unit)? = null
    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        return PeopleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.user_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val   currentUser = differ.currentList[position]

        holder.itemView.apply {
            glide.load(currentUser.image).error(R.drawable.default_user).into(user_image)
            user_name.text=currentUser.name
            user_bio.text=currentUser.bio

            setOnClickListener {
                onItemClickListener?.let { it(currentUser) }
            }

        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}