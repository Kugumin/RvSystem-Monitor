package com.rve.systemmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rve.systemmonitor.domain.repository.SettingsRepository
import com.rve.systemmonitor.utils.ThemeMode
import com.rve.systemmonitor.utils.VibrationIntensity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsRepository: SettingsRepository) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM,
        )

    val amoledMode: StateFlow<Boolean> = settingsRepository.amoledMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val hapticFeedbackEnabled: StateFlow<Boolean> = settingsRepository.hapticFeedbackEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true,
        )

    val vibrationIntensity: StateFlow<VibrationIntensity> = settingsRepository.vibrationIntensity
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VibrationIntensity.LIGHT,
        )

    val cpuRefreshDelay: StateFlow<Long> = settingsRepository.cpuRefreshDelay
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 3000L,
        )

    val memoryRefreshDelay: StateFlow<Long> = settingsRepository.memoryRefreshDelay
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 3000L,
        )

    val batteryRefreshDelay: StateFlow<Long> = settingsRepository.batteryRefreshDelay
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 1000L,
        )

    val batteryGraphHistorySeconds: StateFlow<Int> = settingsRepository.batteryGraphHistorySeconds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 60,
        )

    val autoUpdateEnabled: StateFlow<Boolean> = settingsRepository.autoUpdateEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true,
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setAmoledMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAmoledMode(enabled)
        }
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setHapticFeedbackEnabled(enabled)
        }
    }

    fun setVibrationIntensity(intensity: VibrationIntensity) {
        viewModelScope.launch {
            settingsRepository.setVibrationIntensity(intensity)
        }
    }

    fun setCpuRefreshDelay(delayMillis: Long) {
        viewModelScope.launch {
            settingsRepository.setCpuRefreshDelay(delayMillis)
        }
    }

    fun setMemoryRefreshDelay(delayMillis: Long) {
        viewModelScope.launch {
            settingsRepository.setMemoryRefreshDelay(delayMillis)
        }
    }

    fun setBatteryRefreshDelay(delayMillis: Long) {
        viewModelScope.launch {
            settingsRepository.setBatteryRefreshDelay(delayMillis)
        }
    }

    fun setBatteryGraphHistorySeconds(seconds: Int) {
        viewModelScope.launch {
            settingsRepository.setBatteryGraphHistorySeconds(seconds)
        }
    }

    fun setAutoUpdateEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoUpdateEnabled(enabled)
        }
    }
}
