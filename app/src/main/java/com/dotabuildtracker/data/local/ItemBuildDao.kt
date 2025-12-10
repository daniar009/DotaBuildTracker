package com.dotabuildtracker.data.local

import androidx.room.*
import com.dotabuildtracker.data.model.ItemBuild
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemBuildDao {
    @Query("SELECT * FROM item_builds WHERE playerId = :playerId ORDER BY timestamp DESC")
    fun getBuildsByPlayerId(playerId: String): Flow<List<ItemBuild>>
    
    @Query("SELECT * FROM item_builds WHERE playerId = :playerId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestBuild(playerId: String): ItemBuild?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuild(build: ItemBuild)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuilds(builds: List<ItemBuild>)
    
    @Query("DELETE FROM item_builds WHERE playerId = :playerId")
    suspend fun deleteBuildsByPlayerId(playerId: String)
    
    @Query("SELECT * FROM item_builds")
    fun getAllBuilds(): Flow<List<ItemBuild>>
}

