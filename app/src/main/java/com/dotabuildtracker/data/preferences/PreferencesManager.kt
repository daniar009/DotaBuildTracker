package com.dotabuildtracker.data.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "DotaBuildTrackerPrefs",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_LAST_PLAYER_ID = "last_player_id"
        private const val KEY_LAST_UPDATE_TIME = "last_update_time"
        private const val KEY_AUTO_UPDATE_ENABLED = "auto_update_enabled"
    }
    
    fun saveLastPlayerId(playerId: String) {
        prefs.edit().putString(KEY_LAST_PLAYER_ID, playerId).apply()
    }
    
    fun getLastPlayerId(): String? {
        return prefs.getString(KEY_LAST_PLAYER_ID, null)
    }
    
    fun saveLastUpdateTime(time: Long) {
        prefs.edit().putLong(KEY_LAST_UPDATE_TIME, time).apply()
    }
    
    fun getLastUpdateTime(): Long {
        return prefs.getLong(KEY_LAST_UPDATE_TIME, 0)
    }
    
    fun setAutoUpdateEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_UPDATE_ENABLED, enabled).apply()
    }
    
    fun isAutoUpdateEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_UPDATE_ENABLED, true)
    }
}

