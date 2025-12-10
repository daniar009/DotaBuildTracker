package com.dotabuildtracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dotabuildtracker.data.model.ItemBuild
import com.dotabuildtracker.data.repository.ItemBuildRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItemBuildViewModel(
    private val repository: ItemBuildRepository
) : ViewModel() {
    
    private val _builds = MutableLiveData<List<ItemBuild>>()
    val builds: LiveData<List<ItemBuild>> = _builds
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _lastUpdateTime = MutableLiveData<String>()
    val lastUpdateTime: LiveData<String> = _lastUpdateTime
    
    fun loadBuilds(playerId: String) {
        viewModelScope.launch {
            repository.getBuilds(playerId).collect { buildsList ->
                _builds.value = buildsList
                updateLastUpdateTime()
            }
        }
    }
    
    fun fetchBuilds(playerId: String) {
        if (playerId.isBlank()) {
            _error.value = "Please enter a valid player ID"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.fetchAndSaveBuilds(playerId)
            
            result.fold(
                onSuccess = {
                    loadBuilds(playerId)
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to fetch builds"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    private fun updateLastUpdateTime() {
        val timestamp = repository.getLastUpdateTime()
        if (timestamp > 0) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            _lastUpdateTime.value = dateFormat.format(Date(timestamp))
        } else {
            _lastUpdateTime.value = "Never"
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}

