package com.example.socialmediaapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.text.format.DateFormat
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
import com.example.socialmediaapp.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.android.synthetic.main.item_comment.view.*
import java.util.*
import javax.inject.Inject

class AdapterComment @Inject constructor (
    @ApplicationContext var context: Context,
    var repository: Repository,
    private val glide: RequestManager,
    private var refDatabase: DatabaseReference,
    private var refStorage: StorageReference,
    private var auth: FirebaseAuth
) : RecyclerView.Adapter<AdapterComment.CommentViewHolder>() {

    var myUserId =auth.currentUser?.uid

    var postId :String =""






    inner class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

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
     return   CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_comment,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val   curComment = differ.currentList[position]

        val cal = Calendar.getInstance(Locale.getDefault())
        if (curComment.timeStamp != null) {
            cal.timeInMillis = curComment.timeStamp!!.toLong()
        }
        val commentTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString()


        holder.itemView.apply {
            item_comment.text=curComment.comment
            item_comment_nameTv.text=curComment.userName
            item_comment_timeTv.text=commentTime




            glide.load(curComment.userImage).error(R.drawable.ic_profile).into(item_comment_avatarTv)

            item_comment_long_click.setOnLongClickListener {
                if (myUserId == curComment.userId) {
                    //my comment
                    //show delete dialog
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Delete")
                    builder.setMessage("are you sure to delete this comment")
                    builder.setPositiveButton("Delete") { dialog, which ->
                        deleteComment(curComment.commentId)

                    }
                    builder.setNegativeButton(
                        "Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    val myAlertDialog: AlertDialog = builder.create()
                    myAlertDialog.setOnShowListener {
                        myAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorGreen));
                        myAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.red));
                        myAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(resources.getColor(R.color.red));
                    }
                    myAlertDialog.show()
                } else {
                    Toast.makeText(context, "Can't delete others's comments.", Toast.LENGTH_SHORT)
                        .show()
                }
                false
            }
        }

    }

    private fun deleteComment(commentId: String?) {
        val ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId)
        ref.child("Comments").child(commentId!!).removeValue() // it will delete the comment

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

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}