package com.example.socialmediaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.UserLayoutBinding
import com.example.socialmediaapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AdapterPeople @Inject constructor(
    @ApplicationContext var context: Context,
    private val glide: RequestManager,
    private var refDatabase: DatabaseReference,
    private var refStorage: StorageReference,
    private var auth: FirebaseAuth
) : RecyclerView.Adapter<AdapterPeople.PeopleViewHolder>() {

    inner class PeopleViewHolder(val binding: UserLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, DIFF_CALLBACK)
    private var onItemClickListener: ((User) -> Unit)? = null

    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder =
        PeopleViewHolder(UserLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val currentUser = differ.currentList[position]

        holder.binding.apply {
            glide.load(currentUser.image).error(R.drawable.default_user).into(userImage)
            userName.text = currentUser.name
            userBio.text = currentUser.bio

            root.setOnClickListener { onItemClickListener?.invoke(currentUser) }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}
