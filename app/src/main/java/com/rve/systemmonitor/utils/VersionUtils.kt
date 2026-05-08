package com.rve.systemmonitor.utils

object VersionUtils {
    /**
     * Compares two version strings.
     * Returns 1 if v1 > v2, -1 if v1 < v2, 0 if v1 == v2.
     * Handles versions like "0.2-beta" and "v0.3.0".
     */
    fun compareVersions(v1: String, v2: String): Int {
        val cleanV1 = v1.removePrefix("v").substringBefore("-")
        val cleanV2 = v2.removePrefix("v").substringBefore("-")

        val parts1 = cleanV1.split(".").map { it.toIntOrNull() ?: 0 }
        val parts2 = cleanV2.split(".").map { it.toIntOrNull() ?: 0 }

        val maxLength = maxOf(parts1.size, parts2.size)
        for (i in 0 until maxLength) {
            val part1 = parts1.getOrElse(i) { 0 }
            val part2 = parts2.getOrElse(i) { 0 }
            if (part1 > part2) return 1
            if (part1 < part2) return -1
        }

        // If numeric parts are equal, check for suffixes (simple check)
        val hasSuffix1 = v1.contains("-")
        val hasSuffix2 = v2.contains("-")

        return if (!hasSuffix1 && hasSuffix2) 1
        else if (hasSuffix1 && !hasSuffix2) -1
        else 0
    }
}
