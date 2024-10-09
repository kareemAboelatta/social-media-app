package com.example.main.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.domain.model.Attachment
import com.example.common.ui.utils.getRelativeTimeSpan
import com.example.core.ui.utils.loadCircleImageFromUrl
import com.example.main.databinding.ItemPostWithFourAttachmentBinding
import com.example.main.databinding.ItemPostWithMultiAttachmentsBinding
import com.example.main.databinding.ItemPostWithSingleAttachmentBinding
import com.example.main.databinding.ItemPostWithThreeAttachmentBinding
import com.example.main.databinding.ItemPostWithTwoAttachmentBinding
import com.example.main.domain.model.Post


class PostsAdapter(
    val onPostClicked: (post: Post, position: Int) -> Unit,
    val onAttachmentClicked: (attachments: List<Attachment>, position: Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val differCallback = object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<Post>) {
        differ.submitList(list)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SINGLE_ATTACHMENT -> {
                SingleAttachmentViewHolder(
                    ItemPostWithSingleAttachmentBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            }

            VIEW_TYPE_TWO_ATTACHMENT -> {
                TwoAttachmentsViewHolder(
                    ItemPostWithTwoAttachmentBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            }

            VIEW_TYPE_THREE_ATTACHMENT -> {
                ThreeAttachmentsViewHolder(
                    ItemPostWithThreeAttachmentBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            }

            VIEW_TYPE_FOUR_ATTACHMENT -> {
                FourAttachmentsViewHolder(
                    ItemPostWithFourAttachmentBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            }

            VIEW_TYPE_MULTI_ATTACHMENT -> {
                MultiAttachmentsViewHolder(
                    ItemPostWithMultiAttachmentsBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            }

            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size // +1 for the add button
    }

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position].attachments.size) {
            1 -> VIEW_TYPE_SINGLE_ATTACHMENT
            2 -> VIEW_TYPE_TWO_ATTACHMENT
            3 -> VIEW_TYPE_THREE_ATTACHMENT
            4 -> VIEW_TYPE_FOUR_ATTACHMENT
            else -> {
                VIEW_TYPE_MULTI_ATTACHMENT
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_SINGLE_ATTACHMENT -> (holder as SingleAttachmentViewHolder).bind(differ.currentList[position])
            VIEW_TYPE_TWO_ATTACHMENT -> (holder as TwoAttachmentsViewHolder).bind(differ.currentList[position])
            VIEW_TYPE_THREE_ATTACHMENT -> (holder as ThreeAttachmentsViewHolder).bind(differ.currentList[position])
            VIEW_TYPE_FOUR_ATTACHMENT -> (holder as FourAttachmentsViewHolder).bind(differ.currentList[position])
            VIEW_TYPE_MULTI_ATTACHMENT -> (holder as MultiAttachmentsViewHolder).bind(differ.currentList[position])
        }
    }


    inner class SingleAttachmentViewHolder(private val binding: ItemPostWithSingleAttachmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            with(binding) {
                tvCaption.text = post.caption
                rvAttachments.apply {
                    adapter = PostFixedHeightAttachmentsAdapter(
                        onAttachmentClicked = { attachments, pos ->
                            onAttachmentClicked(attachments, pos)
                        }
                    ).apply {
                        submitList(post.attachments)
                    }
                }
                with(postHeader) {
                    postUserPicture.loadCircleImageFromUrl(post.user.image)
                    postUserName.text = post.user.name
                    bio.text = post.user.bio
                    time.text = itemView.context.getRelativeTimeSpan(post.postInfo.createAt?:0)
                }
            }

        }
    }

    inner class TwoAttachmentsViewHolder(private val binding: ItemPostWithTwoAttachmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            with(binding) {
                tvCaption.text = post.caption
                rvAttachments.apply {
                    adapter = PostAttachmentsAdapter(
                        onAttachmentClicked = { attachments, pos ->
                            onAttachmentClicked(attachments, pos)
                        }
                    ).apply {
                        submitList(post.attachments)
                    }
                }
                with(postHeader) {
                    postUserPicture.loadCircleImageFromUrl(post.user.image)
                    postUserName.text = post.user.name
                    bio.text = post.user.bio
                    time.text = itemView.context.getRelativeTimeSpan(post.postInfo.createAt?:0)
                }
            }


        }
    }

    inner class ThreeAttachmentsViewHolder(private val binding: ItemPostWithThreeAttachmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            with(binding) {
                tvCaption.text = post.caption

                val layoutManager = GridLayoutManager(binding.root.context, 2)
                layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (position) {
                            0, 1 -> 1 // First two items take 1 span each
                            2 -> 2 // Third item takes 2 spans (full width)
                            else -> 1
                        }
                    }
                }

                rvAttachments.layoutManager = layoutManager
                rvAttachments.adapter = PostFixedHeightAttachmentsAdapter(
                    onAttachmentClicked = { attachments, pos ->
                        onAttachmentClicked(post.attachments, pos)
                    }
                ).apply {
                    submitList(post.attachments)
                }

                with(postHeader) {
                    postUserPicture.loadCircleImageFromUrl(post.user.image)
                    postUserName.text = post.user.name
                    bio.text = post.user.bio
                    time.text = itemView.context.getRelativeTimeSpan(post.postInfo.createAt?:0)
                }
            }
        }
    }

    inner class FourAttachmentsViewHolder(private val binding: ItemPostWithFourAttachmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            with(binding) {
                tvCaption.text = post.caption

                binding.rvAttachments.apply {
                    adapter = PostFixedHeightAttachmentsAdapter(
                        onAttachmentClicked = { attachments, pos ->
                            onAttachmentClicked(attachments, pos)
                        }
                    ).apply {
                        submitList(post.attachments)
                    }
                }

                with(postHeader) {
                    postUserPicture.loadCircleImageFromUrl(post.user.image)
                    postUserName.text = post.user.name
                    bio.text = post.user.bio
                    time.text = itemView.context.getRelativeTimeSpan(post.postInfo.createAt?:0)
                }
                with(postFooter) {
                    postCommentTV.text = "${post.postInfo.postComments} comments"
                    postLikesTV.text = "${post.postInfo.postLikes} likes"
                }
            }


        }
    }

    inner class MultiAttachmentsViewHolder(private val binding: ItemPostWithMultiAttachmentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            with(binding) {
                tvCaption.text = post.caption

                binding.rvAttachments.apply {
                    adapter = PostMultiAttachmentsAdapter(
                        onAttachmentClicked = { attachments, pos ->
                            onAttachmentClicked(attachments, pos)
                        }
                    ).apply {
                        submitList(post.attachments)
                    }
                }

                with(postHeader) {
                    postUserPicture.loadCircleImageFromUrl(post.user.image)
                    postUserName.text = post.user.name
                    bio.text = post.user.bio
                    time.text = itemView.context.getRelativeTimeSpan(post.postInfo.createAt?:0)
                }
                with(postFooter) {
                    postCommentTV.text = "${post.postInfo.postComments} comments"
                    postLikesTV.text = "${post.postInfo.postLikes} likes"
                }
            }


        }
    }


    companion object {
        private const val VIEW_TYPE_SINGLE_ATTACHMENT = 0
        private const val VIEW_TYPE_TWO_ATTACHMENT = 1
        private const val VIEW_TYPE_THREE_ATTACHMENT = 2
        private const val VIEW_TYPE_FOUR_ATTACHMENT = 3
        private const val VIEW_TYPE_MULTI_ATTACHMENT = 4
    }

}
