package com.dotabuildtracker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
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
        private val itemIconAdapter = ItemIconAdapter()
        
        init {
            // Setup nested RecyclerView for item icons with GridLayoutManager
            // Show 3 items per row (since we now have names below icons, they need more width)
            binding.rvItems.apply {
                layoutManager = GridLayoutManager(binding.root.context, 3)
                adapter = itemIconAdapter
                isNestedScrollingEnabled = false
            }
        }
        
        fun bind(build: ItemBuild) {
            binding.tvHeroName.text = build.heroName
            binding.tvMatchCount.text = "Matches: ${build.matchCount}"
            
            // Filter out "No common items found" and empty items
            val validItems = build.items.filter { 
                it.isNotBlank() && it != "No common items found" 
            }
            itemIconAdapter.submitList(validItems)
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

