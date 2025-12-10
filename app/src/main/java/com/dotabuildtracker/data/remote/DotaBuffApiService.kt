package com.dotabuildtracker.data.remote

import com.dotabuildtracker.data.model.Hero
import com.dotabuildtracker.data.model.Item
import com.dotabuildtracker.data.model.MatchDetail
import com.dotabuildtracker.data.model.PlayerMatch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DotaBuffApiService {
    // Get recent matches for a player (limit to 10 for performance)
    @GET("players/{playerId}/matches?limit=10")
    suspend fun getPlayerMatches(@Path("playerId") playerId: String): Response<List<PlayerMatch>>
    
    // Get detailed match information
    @GET("matches/{matchId}")
    suspend fun getMatchDetails(@Path("matchId") matchId: Long): Response<MatchDetail>
    
    // Get hero information
    @GET("heroes")
    suspend fun getHeroes(): Response<List<Hero>>
    
    // Get item information
    @GET("constants/items")
    suspend fun getItems(): Response<Map<String, Item>>
}
