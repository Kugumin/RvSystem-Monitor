package com.rve.systemmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rve.systemmonitor.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SetupViewModel @Inject constructor(private val settingsRepository: SettingsRepository) : ViewModel() {

    val autoUpdateEnabled: StateFlow<Boolean> = settingsRepository.autoUpdateEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true,
        )

    fun completeSetup() {
        viewModelScope.launch {
            settingsRepository.setSetupCompleted(true)
        }
    }

    fun setAutoUpdateEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoUpdateEnabled(enabled)
        }
    }
}
