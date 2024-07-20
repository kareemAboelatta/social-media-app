package com.example.more.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.more.databinding.ItemMoreBinding

class MoreAdapter(
    private val items: List<MoreItems>,
    private val onItemClick: (MoreItems) -> Unit
) : RecyclerView.Adapter<MoreAdapter.MoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreViewHolder {
        val binding = ItemMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoreViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class MoreViewHolder(private val binding: ItemMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MoreItems) {
            binding.icon.setImageResource(item.icon)
            binding.title.setText(item.title)
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }
}
