package com.rve.systemmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rve.systemmonitor.domain.repository.CpuRepository
import com.rve.systemmonitor.domain.repository.HardwareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeViewModel @Inject constructor(private val hardwareRepository: HardwareRepository, private val cpuRepository: CpuRepository) :
    ViewModel() {

    private val staticHomeData = HomeUiState(
        device = hardwareRepository.getDeviceInfo(),
        os = hardwareRepository.getOSInfo(),
        display = hardwareRepository.getDisplayInfo(),
        cpu = cpuRepository.getCpuInfo(),
        gpu = hardwareRepository.getGpuInfo(),
    )

    val uiState: StateFlow<HomeUiState> = flowOf(staticHomeData)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(),
        )
}
