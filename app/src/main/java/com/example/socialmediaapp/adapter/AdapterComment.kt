package com.example.socialmediaapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.models.Comment
import com.example.socialmediaapp.models.Post
import com.example.socialmediaapp.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.socialmediaapp.databinding.ItemCommentBinding

import java.util.*
import javax.inject.Inject

class AdapterComment  (
    var context: Context,
    var onDelete: (comment: Comment) -> Unit,
    private var myUserId: String,
    private val glide: RequestManager,


    ) : RecyclerView.Adapter<AdapterComment.CommentViewHolder>() {

    var postId :String =""



    inner class CommentViewHolder(val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root)

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Comment>() {

            override fun areItemsTheSame(oldItem: Comment, newItem: Comment) =
                oldItem.commentId == newItem.commentId

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment) =
                oldItem.commentId == newItem.commentId

        }
    }

    val differ = AsyncListDiffer(this, DIFF_CALLBACK)




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }



    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val   curComment = differ.currentList[position]

        val cal = Calendar.getInstance(Locale.getDefault())
        cal.timeInMillis = curComment.timeStamp.toLong()
        val commentTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString()


        holder.binding.apply {

            itemComment.text = curComment.comment
            itemCommentNameTv.text = curComment.userName
            itemCommentTimeTv.text = commentTime

            glide.load(curComment.userImage).error(R.drawable.ic_profile).into(itemCommentAvatarTv)


            itemCommentLongClick.setOnLongClickListener {
                if (myUserId == curComment.userId) {
                    context.createCommentDeletionDialog {onDelete(curComment) }.show()
                } else {
                    Toast.makeText(context, "Can't delete other's comments.", Toast.LENGTH_SHORT).show()
                }
                false
            }


        }

    }


    private fun Context.createCommentDeletionDialog(onDelete: () -> Unit): AlertDialog {
        val builder = AlertDialog.Builder(this)
        return builder.setTitle("Delete")
            .setMessage("are you sure to delete this comment")
            .setPositiveButton("Delete") { _, _ -> onDelete() }
            .setNegativeButton("Cancel", null)
            .create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this@createCommentDeletionDialog.getColorCompat(R.color.red))
                    getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this@createCommentDeletionDialog.getColorCompat(R.color.colorGreen))
                }
            }
    }


    private fun Context.getColorCompat(color: Int) = ContextCompat.getColor(this, color)


/*    private fun deleteComment(commentId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId)
        ref.child("Comments").child(commentId).removeValue() // it will delete the comment

        //now update the comment count
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val comments = "" + dataSnapshot.child("postComments").value
                val newCommentVal = comments.toInt() - 1
                ref.child("postComments").setValue(newCommentVal)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        Toast.makeText(context, "Comment deleted..", Toast.LENGTH_SHORT).show()
    }*/

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}