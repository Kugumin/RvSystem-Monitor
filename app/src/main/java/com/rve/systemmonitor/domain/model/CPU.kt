package com.rve.systemmonitor.domain.model

import androidx.compose.runtime.Immutable
import java.util.Locale
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class CPU(
    val manufacturer: String = "unknown",
    val model: String = "unknown",
    val cores: Int = 0,
    val hardware: String = "unknown",
    val board: String = "unknown",
    val architecture: String = "unknown",
    val temperature: Double = 0.0,
    val coreDetails: ImmutableList<CoreDetail> = persistentListOf(),
)

@Immutable
data class CoreDetail(
    val id: Int,
    val currentFreqKhz: Long = 0,
    val minFreqKhz: Long = 0,
    val maxFreqKhz: Long = 0,
    val governor: String = "unknown",
    val temperature: Double = 0.0,
) {
    // Helper for easy display when needed, but we prefer formatting in UI
    val currentFreq: String get() = formatFrequency(currentFreqKhz)
    val minFreq: String get() = formatFrequency(minFreqKhz)
    val maxFreq: String get() = formatFrequency(maxFreqKhz)

    private fun formatFrequency(freqKhz: Long): String {
        return if (freqKhz >= 1_000_000) {
            String.format(Locale.US, "%.2f GHz", freqKhz / 1_000_000.0)
        } else {
            "${freqKhz / 1000} MHz"
        }
    }
}
