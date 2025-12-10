package com.dotabuildtracker.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dotabuildtracker.data.local.AppDatabase
import com.dotabuildtracker.data.preferences.PreferencesManager
import com.dotabuildtracker.data.remote.RetrofitClient
import com.dotabuildtracker.data.repository.ItemBuildRepository

class DailyUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            val preferencesManager = PreferencesManager(applicationContext)
            val lastPlayerId = preferencesManager.getLastPlayerId()
            
            if (lastPlayerId != null && preferencesManager.isAutoUpdateEnabled()) {
                val database = AppDatabase.getDatabase(applicationContext)
                val repository = ItemBuildRepository(
                    RetrofitClient.apiService,
                    database.itemBuildDao(),
                    preferencesManager
                )
                
                repository.fetchAndSaveBuilds(lastPlayerId)
                Result.success()
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

