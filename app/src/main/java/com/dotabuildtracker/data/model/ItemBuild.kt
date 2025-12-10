package com.dotabuildtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "item_builds")
data class ItemBuild(
    @PrimaryKey
    val id: String,
    val playerId: String,
    val heroName: String,
    val heroId: Int,
    val matchCount: Int,
    val items: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

// OpenDota API Response Models
data class PlayerMatch(
    @SerializedName("match_id")
    val matchId: Long,
    @SerializedName("hero_id")
    val heroId: Int,
    @SerializedName("kills")
    val kills: Int,
    @SerializedName("deaths")
    val deaths: Int,
    @SerializedName("assists")
    val assists: Int,
    @SerializedName("start_time")
    val startTime: Long
)

data class MatchDetail(
    @SerializedName("match_id")
    val matchId: Long,
    @SerializedName("players")
    val players: List<MatchPlayer>
)

data class MatchPlayer(
    @SerializedName("account_id")
    val accountId: Long?,
    @SerializedName("hero_id")
    val heroId: Int,
    @SerializedName("player_slot")
    val playerSlot: Int,
    @SerializedName("item_0")
    val item0: Int,
    @SerializedName("item_1")
    val item1: Int,
    @SerializedName("item_2")
    val item2: Int,
    @SerializedName("item_3")
    val item3: Int,
    @SerializedName("item_4")
    val item4: Int,
    @SerializedName("item_5")
    val item5: Int,
    @SerializedName("backpack_0")
    val backpack0: Int,
    @SerializedName("backpack_1")
    val backpack1: Int,
    @SerializedName("backpack_2")
    val backpack2: Int
)

data class Hero(
    @SerializedName("id")
    val id: Int,
    @SerializedName("localized_name")
    val localizedName: String
)

data class Item(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("localized_name")
    val localizedName: String? = null,
    @SerializedName("dname")
    val displayName: String? = null
)
