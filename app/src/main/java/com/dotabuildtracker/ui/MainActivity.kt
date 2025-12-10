package com.dotabuildtracker.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dotabuildtracker.data.local.AppDatabase
import com.dotabuildtracker.data.preferences.PreferencesManager
import com.dotabuildtracker.data.remote.RetrofitClient
import com.dotabuildtracker.data.repository.ItemBuildRepository
import com.dotabuildtracker.databinding.ActivityMainBinding
import com.dotabuildtracker.ui.adapter.ItemBuildAdapter
import com.dotabuildtracker.ui.viewmodel.ItemBuildViewModel
import com.dotabuildtracker.utils.WorkManagerHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ItemBuildAdapter
    
    private val viewModel: ItemBuildViewModel by viewModels {
        val database = AppDatabase.getDatabase(this)
        val repository = ItemBuildRepository(
            RetrofitClient.apiService,
            database.itemBuildDao(),
            PreferencesManager(this)
        )
        ItemBuildViewModelFactory(repository)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        // Load last used player ID
        val lastPlayerId = PreferencesManager(this).getLastPlayerId()
        if (lastPlayerId != null) {
            binding.etPlayerId.setText(lastPlayerId)
            viewModel.loadBuilds(lastPlayerId)
        }
        
        // Schedule daily updates
        WorkManagerHelper.scheduleDailyUpdate(this)
    }
    
    private fun setupRecyclerView() {
        adapter = ItemBuildAdapter()
        binding.recyclerViewBuilds.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewBuilds.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.builds.observe(this) { builds ->
            if (builds.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.recyclerViewBuilds.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.recyclerViewBuilds.visibility = View.VISIBLE
                adapter.submitList(builds)
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnFetch.isEnabled = !isLoading
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        
        viewModel.lastUpdateTime.observe(this) { time ->
            binding.tvLastUpdate.text = "Last updated: $time"
        }
    }
    
    private fun setupClickListeners() {
        binding.btnFetch.setOnClickListener {
            val playerId = binding.etPlayerId.text.toString().trim()
            viewModel.fetchBuilds(playerId)
        }
        
        binding.btnRefresh.setOnClickListener {
            val playerId = binding.etPlayerId.text.toString().trim()
            if (playerId.isNotEmpty()) {
                viewModel.fetchBuilds(playerId)
            } else {
                Toast.makeText(this, "Please enter a player ID first", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

