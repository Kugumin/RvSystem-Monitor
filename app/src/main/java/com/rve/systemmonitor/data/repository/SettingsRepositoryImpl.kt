package com.rve.systemmonitor.data.repository

import android.app.Application
import com.rve.systemmonitor.domain.repository.SettingsRepository
import com.rve.systemmonitor.utils.SettingsPreferences
import com.rve.systemmonitor.utils.ThemeMode
import com.rve.systemmonitor.utils.VibrationIntensity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SettingsRepositoryImpl @Inject constructor(application: Application) : SettingsRepository {

    private val settingsPreferences = SettingsPreferences(application)

    override val themeMode: Flow<ThemeMode> = settingsPreferences.themeModeFlow

    override val amoledMode: Flow<Boolean> = settingsPreferences.amoledModeFlow

    override val hapticFeedbackEnabled: Flow<Boolean> = settingsPreferences.hapticFeedbackEnabledFlow

    override val vibrationIntensity: Flow<VibrationIntensity> = settingsPreferences.vibrationIntensityFlow

    override val isSetupCompleted: Flow<Boolean> = settingsPreferences.isSetupCompletedFlow

    override val cpuRefreshDelay: Flow<Long> = settingsPreferences.cpuRefreshDelayFlow

    override val memoryRefreshDelay: Flow<Long> = settingsPreferences.memoryRefreshDelayFlow

    override val gpuRefreshDelay: Flow<Long> = settingsPreferences.gpuRefreshDelayFlow

    override val batteryRefreshDelay: Flow<Long> = settingsPreferences.batteryRefreshDelayFlow

    override val batteryGraphHistorySeconds: Flow<Int> = settingsPreferences.batteryGraphHistorySecondsFlow

    override val autoUpdateEnabled: Flow<Boolean> = settingsPreferences.autoUpdateEnabledFlow

    override val useShizuku: Flow<Boolean> = settingsPreferences.useShizukuFlow

    override val updatesPausedUntil: Flow<Long> = settingsPreferences.updatesPausedUntilFlow

    override suspend fun setThemeMode(mode: ThemeMode) {
        settingsPreferences.saveThemeMode(mode)
    }

    override suspend fun setAmoledMode(enabled: Boolean) {
        settingsPreferences.saveAmoledMode(enabled)
    }

    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        settingsPreferences.saveHapticFeedbackEnabled(enabled)
    }

    override suspend fun setVibrationIntensity(intensity: VibrationIntensity) {
        settingsPreferences.saveVibrationIntensity(intensity)
    }

    override suspend fun setSetupCompleted(completed: Boolean) {
        settingsPreferences.saveSetupCompleted(completed)
    }

    override suspend fun setCpuRefreshDelay(delayMillis: Long) {
        settingsPreferences.saveCpuRefreshDelay(delayMillis)
    }

    override suspend fun setMemoryRefreshDelay(delayMillis: Long) {
        settingsPreferences.saveMemoryRefreshDelay(delayMillis)
    }

    override suspend fun setGpuRefreshDelay(delayMillis: Long) {
        settingsPreferences.saveGpuRefreshDelay(delayMillis)
    }

    override suspend fun setBatteryRefreshDelay(delayMillis: Long) {
        settingsPreferences.saveBatteryRefreshDelay(delayMillis)
    }

    override suspend fun setBatteryGraphHistorySeconds(seconds: Int) {
        settingsPreferences.saveBatteryGraphHistorySeconds(seconds)
    }

    override suspend fun setAutoUpdateEnabled(enabled: Boolean) {
        settingsPreferences.saveAutoUpdateEnabled(enabled)
    }

    override suspend fun setUseShizuku(enabled: Boolean) {
        settingsPreferences.saveUseShizuku(enabled)
    }

    override suspend fun setUpdatesPausedUntil(timestampMillis: Long) {
        settingsPreferences.saveUpdatesPausedUntil(timestampMillis)
    }
}
