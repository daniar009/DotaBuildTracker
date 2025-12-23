package com.dotabuildtracker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dotabuildtracker.R
import com.dotabuildtracker.databinding.ItemIconBinding
import kotlin.random.Random

class ItemIconAdapter : ListAdapter<String, ItemIconAdapter.ItemIconViewHolder>(ItemIconDiffCallback()) {
    
    // List of category icon drawable resources
    private val categoryIcons = listOf(
        R.drawable.icon_staff,
        R.drawable.icon_sword,
        R.drawable.icon_armor,
        R.drawable.icon_utility
    )
    
    // Map to store icon assignments for consistent display (based on item name hash)
    private val iconMap = mutableMapOf<String, Int>()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemIconViewHolder {
        val binding = ItemIconBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemIconViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ItemIconViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ItemIconViewHolder(
        private val binding: ItemIconBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemName: String) {
            // Use consistent icon based on item name hash (so same item always gets same icon)
            val iconResource = iconMap.getOrPut(itemName) {
                // Use item name hash to deterministically select an icon
                categoryIcons[Math.abs(itemName.hashCode()) % categoryIcons.size]
            }
            
            binding.ivItemIcon.setImageResource(iconResource)
            binding.tvItemName.text = itemName
        }
    }
    
    class ItemIconDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
        
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
