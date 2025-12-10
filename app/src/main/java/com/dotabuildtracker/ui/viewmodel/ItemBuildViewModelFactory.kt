package com.dotabuildtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dotabuildtracker.data.repository.ItemBuildRepository

class ItemBuildViewModelFactory(
    private val repository: ItemBuildRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemBuildViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemBuildViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

