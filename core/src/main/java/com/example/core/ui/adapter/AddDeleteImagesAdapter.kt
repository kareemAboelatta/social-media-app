package com.example.core.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.core.databinding.ItemAddImageBinding
import com.example.core.databinding.PhotoItemLayoutBinding
import com.example.core.ui.utils.loadImageFromUrl


class AddDeleteImagesAdapter(
    val removeUriImageOnClick: (uri: String, position: Int) -> Unit,
    val onImageClicked: (photo: String ) -> Unit,
    val addImageOnClick: () -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var uriList: List<String> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_ADD_IMAGE -> {
                AddButtonViewHolder(ItemAddImageBinding.inflate(inflater, parent, false))
            }

            ITEM_URI_IMAGE -> {
                ImagesUriViewHolder(PhotoItemLayoutBinding.inflate(inflater, parent, false))
            }

            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun getItemCount(): Int {
        return uriList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return ITEM_ADD_IMAGE
        } else if (position > uriList.size && position < itemCount) {
            return ITEM_URI_IMAGE
        }
        return ITEM_URI_IMAGE
    }

    companion object {
        private const val ITEM_ADD_IMAGE = 0
        private const val ITEM_URI_IMAGE = 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_ADD_IMAGE -> (holder as AddButtonViewHolder).bind()
            ITEM_URI_IMAGE -> (holder as ImagesUriViewHolder).bind(uriList[position - 1])
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitUriImagesList(list: List<String>) {
        this.uriList = list
        notifyDataSetChanged()
    }

    inner class ImagesUriViewHolder(private val binding: PhotoItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.imageView.loadImageFromUrl(item)

            binding.removePhotoBtn.setOnClickListener {
                removeUriImageOnClick(
                    item,
                    layoutPosition - 1,
                )
                notifyItemRemoved(layoutPosition - 1)
            }

            binding.imageView.setOnClickListener {
                onImageClicked(item)
            }
        }
    }

    inner class AddButtonViewHolder(private val binding: ItemAddImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.addImageLayout.setOnClickListener {
                addImageOnClick()
                notifyDataSetChanged()
            }
        }
    }
}
