package com.rve.systemmonitor.domain.repository

import com.rve.systemmonitor.utils.ThemeMode
import com.rve.systemmonitor.utils.VibrationIntensity
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val themeMode: Flow<ThemeMode>
    val hapticFeedbackEnabled: Flow<Boolean>
    val vibrationIntensity: Flow<VibrationIntensity>
    val isSetupCompleted: Flow<Boolean>
    val cpuRefreshDelay: Flow<Long>
    val memoryRefreshDelay: Flow<Long>
    val batteryRefreshDelay: Flow<Long>
    val batteryGraphHistorySeconds: Flow<Int>
    val autoUpdateEnabled: Flow<Boolean>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setHapticFeedbackEnabled(enabled: Boolean)
    suspend fun setVibrationIntensity(intensity: VibrationIntensity)
    suspend fun setSetupCompleted(completed: Boolean)
    suspend fun setCpuRefreshDelay(delayMillis: Long)
    suspend fun setMemoryRefreshDelay(delayMillis: Long)
    suspend fun setBatteryRefreshDelay(delayMillis: Long)
    suspend fun setBatteryGraphHistorySeconds(seconds: Int)
    suspend fun setAutoUpdateEnabled(enabled: Boolean)
}
