package com.dotabuildtracker.data.repository

import com.dotabuildtracker.data.local.ItemBuildDao
import com.dotabuildtracker.data.model.*
import com.dotabuildtracker.data.preferences.PreferencesManager
import com.dotabuildtracker.data.remote.DotaBuffApiService
import kotlinx.coroutines.flow.Flow

class ItemBuildRepository(
    private val apiService: DotaBuffApiService,
    private val itemBuildDao: ItemBuildDao,
    private val preferencesManager: PreferencesManager
) {
    fun getBuilds(playerId: String): Flow<List<ItemBuild>> {
        return itemBuildDao.getBuildsByPlayerId(playerId)
    }
    
    suspend fun fetchAndSaveBuilds(playerId: String): Result<Unit> {
        return try {
            // Step 1: Get player's recent matches
            val matchesResponse = apiService.getPlayerMatches(playerId)
            if (!matchesResponse.isSuccessful) {
                val errorMsg = when (matchesResponse.code()) {
                    404 -> "Player not found. Please check the player ID."
                    429 -> "Too many requests. Please try again later."
                    else -> "Failed to fetch player matches: ${matchesResponse.code()}"
                }
                return Result.failure(Exception(errorMsg))
            }
            
            if (matchesResponse.body() == null) {
                return Result.failure(Exception("No data received from API"))
            }
            
            val matches = matchesResponse.body()!!
            if (matches.isEmpty()) {
                return Result.failure(Exception("No matches found for this player"))
            }
            
            // Step 2: Get heroes and items mapping
            val heroesResponse = apiService.getHeroes()
            val heroesMap = if (heroesResponse.isSuccessful && heroesResponse.body() != null) {
                heroesResponse.body()!!.associateBy { it.id }
            } else {
                emptyMap()
            }
            
            val itemsResponse = apiService.getItems()
            // OpenDota returns items as Map<String, Item> where keys are like "item_blink"
            // We need to create a mapping from item ID (used in matches) to item name
            val itemIdToNameMap = if (itemsResponse.isSuccessful && itemsResponse.body() != null) {
                val itemsMap = itemsResponse.body()!!
                // Try to extract ID from item name (e.g., "item_blink" -> extract ID)
                // Since OpenDota items might not have direct ID field, we'll use a comprehensive mapping
                buildItemIdMapping(itemsMap)
            } else {
                createItemIdMapping()
            }
            
            // Step 3: Get match details and extract items
            val heroBuildsMap = mutableMapOf<Int, MutableList<List<Int>>>()
            val heroMatchCount = mutableMapOf<Int, Int>()
            val playerIdLong = playerId.toLongOrNull()
            var successfulMatches = 0
            
            for (match in matches) {
                try {
                    val matchDetailResponse = apiService.getMatchDetails(match.matchId)
                    if (matchDetailResponse.isSuccessful && matchDetailResponse.body() != null) {
                        val matchDetail = matchDetailResponse.body()!!
                        
                        // Find the player in the match
                        // For anonymous players, match by hero_id and player_slot from the match list
                        val playerInMatch = matchDetail.players.find { player ->
                            when {
                                // Exact account ID match
                                playerIdLong != null && player.accountId == playerIdLong -> true
                                // Anonymous player: match by hero ID
                                player.accountId == null && player.heroId == match.heroId -> true
                                // Fallback: match by hero ID (less reliable but works)
                                player.heroId == match.heroId -> true
                                else -> false
                            }
                        }
                        
                        if (playerInMatch != null && playerInMatch.heroId == match.heroId) {
                            // Extract items (excluding empty slots where item ID is 0)
                            val items = listOfNotNull(
                                playerInMatch.item0.takeIf { it > 0 },
                                playerInMatch.item1.takeIf { it > 0 },
                                playerInMatch.item2.takeIf { it > 0 },
                                playerInMatch.item3.takeIf { it > 0 },
                                playerInMatch.item4.takeIf { it > 0 },
                                playerInMatch.item5.takeIf { it > 0 }
                            )
                            
                            if (items.isNotEmpty()) {
                                heroBuildsMap.getOrPut(match.heroId) { mutableListOf() }.add(items)
                                heroMatchCount[match.heroId] = (heroMatchCount[match.heroId] ?: 0) + 1
                                successfulMatches++
                            }
                        }
                    }
                    // Add small delay to avoid rate limiting
                    kotlinx.coroutines.delay(100)
                } catch (e: Exception) {
                    // Continue with next match if one fails
                    continue
                }
            }
            
            if (successfulMatches == 0) {
                return Result.failure(Exception("Could not extract item data from matches. Player might have private profile or no recent matches with items."))
            }
            
            // Step 4: Aggregate items by hero and create builds
            val builds = heroBuildsMap.map { (heroId, itemLists) ->
                // Count item frequency across all matches for this hero
                val itemFrequency = mutableMapOf<Int, Int>()
                itemLists.forEach { items ->
                    items.forEach { itemId ->
                        itemFrequency[itemId] = (itemFrequency[itemId] ?: 0) + 1
                    }
                }
                
                // Get most common items (appearing in at least 20% of matches or top 6 items)
                val matchCount = itemLists.size
                val threshold = maxOf(1, (matchCount * 0.2).toInt()) // At least 20% or 1 match
                val commonItems = itemFrequency
                    .filter { it.value >= threshold }
                    .entries
                    .sortedByDescending { it.value }
                    .take(6) // Top 6 items
                    .mapNotNull { entry ->
                        val itemId = entry.key
                        // Convert item ID to name
                        itemIdToNameMap[itemId] ?: "Item $itemId"
                    }
                
                val heroName = heroesMap[heroId]?.localizedName ?: "Hero $heroId"
                
                ItemBuild(
                    id = "${playerId}_$heroId",
                    playerId = playerId,
                    heroName = heroName,
                    heroId = heroId,
                    matchCount = heroMatchCount[heroId] ?: 0,
                    items = if (commonItems.isNotEmpty()) commonItems else listOf("No common items found")
                )
            }
            
            if (builds.isEmpty()) {
                return Result.failure(Exception("No item builds could be created from matches"))
            }
            
            // Step 5: Save to database
            itemBuildDao.deleteBuildsByPlayerId(playerId)
            itemBuildDao.insertBuilds(builds)
            preferencesManager.saveLastPlayerId(playerId)
            preferencesManager.saveLastUpdateTime(System.currentTimeMillis())
            
            Result.success(Unit)
        } catch (e: Exception) {
            // If network fails, try to load from database
            val existingBuilds = itemBuildDao.getLatestBuild(playerId)
            if (existingBuilds != null) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Network error: ${e.message}. Please check your internet connection."))
            }
        }
    }
    
    private fun buildItemIdMapping(itemsMap: Map<String, com.dotabuildtracker.data.model.Item>): Map<Int, String> {
        // OpenDota items map keys are like "item_blink", "item_tango", etc.
        // We need to map these to actual item IDs used in match data
        // Since the API structure varies, we'll use a comprehensive known mapping
        val mapping = mutableMapOf<Int, String>()
        
        // Try to extract from item objects if they have ID
        itemsMap.forEach { (key, item) ->
            val itemName = item.localizedName ?: item.displayName ?: key.replace("item_", "").replace("_", " ").split(" ").joinToString(" ") { 
                it.replaceFirstChar { char -> char.uppercaseChar() }
            }
            item.id?.let { id ->
                if (id > 0) {
                    mapping[id] = itemName
                }
            }
        }
        
        // Merge with fallback mapping for items not in API response
        return (mapping + createItemIdMapping()).distinctBy { it.key }
    }
    
    fun getLastUpdateTime(): Long {
        return preferencesManager.getLastUpdateTime()
    }
    
    // Comprehensive item ID to name mapping based on Dota 2 item IDs
    private fun createItemIdMapping(): Map<Int, String> {
        return mapOf(
            // Basic items
            1 to "Blink Dagger",
            29 to "Boots of Speed",
            34 to "Magic Stick",
            36 to "Magic Wand",
            37 to "Ghost Scepter",
            38 to "Clarity",
            39 to "Healing Salve",
            40 to "Dust of Appearance",
            41 to "Bottle",
            42 to "Observer Ward",
            43 to "Sentry Ward",
            44 to "Tango",
            45 to "Courier",
            46 to "Town Portal Scroll",
            // Boots
            48 to "Boots of Travel",
            50 to "Phase Boots",
            63 to "Power Treads",
            178 to "Arcane Boots",
            182 to "Tranquil Boots",
            // Core items
            65 to "Hand of Midas",
            67 to "Oblivion Staff",
            69 to "Perseverance",
            73 to "Bracer",
            75 to "Wraith Band",
            77 to "Null Talisman",
            79 to "Mekansm",
            81 to "Vladmir's Offering",
            83 to "Buckler",
            85 to "Ring of Basilius",
            87 to "Pipe of Insight",
            89 to "Urn of Shadows",
            91 to "Headdress",
            // Support items
            97 to "Eul's Scepter of Divinity",
            99 to "Force Staff",
            101 to "Dagon",
            103 to "Necronomicon",
            105 to "Aghanim's Scepter",
            107 to "Refresher Orb",
            109 to "Assault Cuirass",
            111 to "Heart of Tarrasque",
            113 to "Black King Bar",
            115 to "Aegis of the Immortal",
            117 to "Shiva's Guard",
            119 to "Bloodstone",
            121 to "Linken's Sphere",
            123 to "Vanguard",
            125 to "Blade Mail",
            127 to "Soul Booster",
            129 to "Hood of Defiance",
            131 to "Divine Rapier",
            133 to "Monkey King Bar",
            135 to "Radiance",
            137 to "Butterfly",
            139 to "Daedalus",
            141 to "Skull Basher",
            143 to "Battle Fury",
            145 to "Manta Style",
            147 to "Crystalys",
            149 to "Armlet of Mordiggian",
            151 to "Shadow Blade",
            153 to "Sange and Yasha",
            155 to "Satanic",
            157 to "Mjollnir",
            159 to "Eye of Skadi",
            161 to "Sange",
            163 to "Helm of the Dominator",
            165 to "Maelstrom",
            167 to "Desolator",
            169 to "Yasha",
            171 to "Mask of Madness",
            172 to "Diffusal Blade",
            175 to "Ethereal Blade",
            177 to "Soul Ring",
            180 to "Octarine Core",
            185 to "Ring of Aquila",
            187 to "Aghanim's Blessing",
            188 to "Guardian Greaves",
            190 to "Rod of Atos",
            192 to "Abyssal Blade",
            194 to "Heaven's Halberd",
            196 to "Ring of Tarrasque",
            198 to "Lotus Orb",
            200 to "Solar Crest",
            204 to "Glimmer Cape",
            206 to "Aeon Disk",
            208 to "Meteor Hammer",
            210 to "Nullifier",
            212 to "Aether Lens",
            214 to "Spirit Vessel",
            216 to "Holy Locket",
            218 to "Kaya",
            220 to "Kaya and Sange",
            222 to "Yasha and Kaya",
            224 to "Trident",
            // Recipes and components
            92 to "Scythe of Vyse",
            94 to "Orchid Malevolence",
            // Additional common items
            2 to "Blades of Attack",
            3 to "Broadsword",
            4 to "Chainmail",
            5 to "Claymore",
            6 to "Helm of Iron Will",
            7 to "Javelin",
            8 to "Mithril Hammer",
            9 to "Platemail",
            10 to "Quarterstaff",
            11 to "Quelling Blade",
            12 to "Ring of Protection",
            13 to "Gauntlets of Strength",
            14 to "Slippers of Agility",
            15 to "Mantle of Intelligence",
            16 to "Iron Branch",
            17 to "Belt of Strength",
            18 to "Band of Elvenskin",
            19 to "Robe of the Magi",
            20 to "Circlet",
            21 to "Ogre Club",
            22 to "Blade of Alacrity",
            23 to "Staff of Wizardry",
            24 to "Ultimate Orb",
            25 to "Gloves of Haste",
            26 to "Morbid Mask",
            27 to "Ring of Regen",
            28 to "Sage's Mask",
            30 to "Gem of True Sight",
            31 to "Cloak",
            32 to "Talisman of Evasion",
            33 to "Cheese",
            51 to "Demon Edge",
            52 to "Eagle",
            53 to "Reaver",
            54 to "Sacred Relic",
            55 to "Hyperstone",
            56 to "Ring of Health",
            57 to "Void Stone",
            58 to "Mystic Staff",
            59 to "Energy Booster",
            60 to "Point Booster",
            61 to "Vitality Booster"
        )
    }
}
