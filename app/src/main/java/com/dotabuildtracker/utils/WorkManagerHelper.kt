package com.dotabuildtracker.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dotabuildtracker.worker.DailyUpdateWorker
import java.util.concurrent.TimeUnit

object WorkManagerHelper {
    private const val WORK_NAME = "daily_update_work"
    
    fun scheduleDailyUpdate(context: Context) {
        val dailyUpdateRequest = PeriodicWorkRequestBuilder<DailyUpdateWorker>(
            1, TimeUnit.DAYS
        ).build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyUpdateRequest
        )
    }
    
    fun cancelDailyUpdate(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}

