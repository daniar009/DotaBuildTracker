package com.dotabuildtracker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dotabuildtracker.data.model.ItemBuild
import com.dotabuildtracker.databinding.ItemBuildBinding

class ItemBuildAdapter : ListAdapter<ItemBuild, ItemBuildAdapter.ItemBuildViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemBuildViewHolder {
        val binding = ItemBuildBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemBuildViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ItemBuildViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ItemBuildViewHolder(
        private val binding: ItemBuildBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(build: ItemBuild) {
            binding.tvHeroName.text = build.heroName
            binding.tvMatchCount.text = "Matches: ${build.matchCount}"
            binding.tvItems.text = build.items.joinToString(", ")
        }
    }
    
    class DiffCallback : DiffUtil.ItemCallback<ItemBuild>() {
        override fun areItemsTheSame(oldItem: ItemBuild, newItem: ItemBuild): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: ItemBuild, newItem: ItemBuild): Boolean {
            return oldItem == newItem
        }
    }
}

