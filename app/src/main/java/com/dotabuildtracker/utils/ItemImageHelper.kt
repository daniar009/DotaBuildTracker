package com.dotabuildtracker.utils

object ItemImageHelper {
    /**
     * Converts an item name to its image URL from OpenDota CDN
     * Format: "Blink Dagger" -> "https://cdn.opendota.com/apps/dota2/images/items/blink_dagger_lg.png"
     */
    fun getItemImageUrl(itemName: String): String {
        // Convert item name to the format used in URLs
        // e.g., "Blink Dagger" -> "blink_dagger"
        val urlName = itemName
            .lowercase()
            .replace("'", "")
            .replace(" ", "_")
            .replace("-", "_")
            .replace("é", "e")
            .replace("ö", "o")
            .trim()
        
        return "https://cdn.opendota.com/apps/dota2/images/items/${urlName}_lg.png"
    }
}
