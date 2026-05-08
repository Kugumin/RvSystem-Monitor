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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainViewModel @Inject constructor(settingsRepository: SettingsRepository) : ViewModel() {
    val uiState: StateFlow<MainUiState> = combine(
        settingsRepository.themeMode,
        settingsRepository.isSetupCompleted,
        settingsRepository.hapticFeedbackEnabled,
        settingsRepository.vibrationIntensity,
        settingsRepository.autoUpdateEnabled,
    ) { theme, setupCompleted, hapticEnabled, vibrationIntensity, autoUpdateEnabled ->
        MainUiState.Success(theme, setupCompleted, hapticEnabled, vibrationIntensity, autoUpdateEnabled)
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )
}

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(
        val themeMode: ThemeMode,
        val isSetupCompleted: Boolean,
        val hapticFeedbackEnabled: Boolean,
        val vibrationIntensity: VibrationIntensity,
        val autoUpdateEnabled: Boolean,
    ) : MainUiState
}
