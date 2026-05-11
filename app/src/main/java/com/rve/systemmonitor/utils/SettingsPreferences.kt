package com.rve.systemmonitor.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsPreferences(private val context: Context) {
    companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val AMOLED_MODE_KEY = booleanPreferencesKey("amoled_mode")
        val IS_SETUP_COMPLETED_KEY = booleanPreferencesKey("is_setup_completed")
        val CPU_REFRESH_DELAY_KEY = longPreferencesKey("cpu_refresh_delay")
        val MEMORY_REFRESH_DELAY_KEY = longPreferencesKey("memory_refresh_delay")
        val BATTERY_REFRESH_DELAY_KEY = longPreferencesKey("battery_refresh_delay")
        val BATTERY_GRAPH_HISTORY_KEY = intPreferencesKey("battery_graph_history")
        val HAPTIC_FEEDBACK_ENABLED_KEY = booleanPreferencesKey("haptic_feedback_enabled")
        val VIBRATION_INTENSITY_KEY = stringPreferencesKey("vibration_intensity")
        val AUTO_UPDATE_ENABLED_KEY = booleanPreferencesKey("auto_update_enabled")
        val UPDATES_PAUSED_UNTIL_KEY = longPreferencesKey("updates_paused_until")
    }

    val autoUpdateEnabledFlow: Flow<Boolean> = context.dataStore.getValueFlow(AUTO_UPDATE_ENABLED_KEY, true)
    val updatesPausedUntilFlow: Flow<Long> = context.dataStore.getValueFlow(UPDATES_PAUSED_UNTIL_KEY, 0L)
    val themeModeFlow: Flow<ThemeMode> = context.dataStore.getEnumFlow(THEME_MODE_KEY, ThemeMode.SYSTEM) { ThemeMode.valueOf(it) }
    val amoledModeFlow: Flow<Boolean> = context.dataStore.getValueFlow(AMOLED_MODE_KEY, false)
    val vibrationIntensityFlow: Flow<VibrationIntensity> = context.dataStore.getEnumFlow(
        VIBRATION_INTENSITY_KEY,
        VibrationIntensity.LIGHT,
    ) { VibrationIntensity.valueOf(it) }
    val hapticFeedbackEnabledFlow: Flow<Boolean> = context.dataStore.getValueFlow(HAPTIC_FEEDBACK_ENABLED_KEY, true)
    val isSetupCompletedFlow: Flow<Boolean> = context.dataStore.getValueFlow(IS_SETUP_COMPLETED_KEY, false)
    val cpuRefreshDelayFlow: Flow<Long> = context.dataStore.getValueFlow(CPU_REFRESH_DELAY_KEY, 3000L)
    val memoryRefreshDelayFlow: Flow<Long> = context.dataStore.getValueFlow(MEMORY_REFRESH_DELAY_KEY, 3000L)
    val batteryRefreshDelayFlow: Flow<Long> = context.dataStore.getValueFlow(BATTERY_REFRESH_DELAY_KEY, 1000L)
    val batteryGraphHistorySecondsFlow: Flow<Int> = context.dataStore.getValueFlow(BATTERY_GRAPH_HISTORY_KEY, 60)

    suspend fun saveThemeMode(mode: ThemeMode) = context.dataStore.setEnum(THEME_MODE_KEY, mode)
    suspend fun saveAmoledMode(enabled: Boolean) = context.dataStore.setValue(AMOLED_MODE_KEY, enabled)
    suspend fun saveSetupCompleted(completed: Boolean) = context.dataStore.setValue(IS_SETUP_COMPLETED_KEY, completed)
    suspend fun saveCpuRefreshDelay(delayMillis: Long) = context.dataStore.setValue(CPU_REFRESH_DELAY_KEY, delayMillis)
    suspend fun saveMemoryRefreshDelay(delayMillis: Long) = context.dataStore.setValue(MEMORY_REFRESH_DELAY_KEY, delayMillis)
    suspend fun saveBatteryRefreshDelay(delayMillis: Long) = context.dataStore.setValue(BATTERY_REFRESH_DELAY_KEY, delayMillis)
    suspend fun saveBatteryGraphHistorySeconds(seconds: Int) = context.dataStore.setValue(BATTERY_GRAPH_HISTORY_KEY, seconds)
    suspend fun saveHapticFeedbackEnabled(enabled: Boolean) = context.dataStore.setValue(HAPTIC_FEEDBACK_ENABLED_KEY, enabled)
    suspend fun saveVibrationIntensity(intensity: VibrationIntensity) = context.dataStore.setEnum(VIBRATION_INTENSITY_KEY, intensity)
    suspend fun saveAutoUpdateEnabled(enabled: Boolean) = context.dataStore.setValue(AUTO_UPDATE_ENABLED_KEY, enabled)
    suspend fun saveUpdatesPausedUntil(timestamp: Long) = context.dataStore.setValue(UPDATES_PAUSED_UNTIL_KEY, timestamp)
}
