package com.rve.systemmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rve.systemmonitor.domain.model.GPU
import com.rve.systemmonitor.domain.repository.HardwareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class GPUViewModel @Inject constructor(private val hardwareRepository: HardwareRepository) : ViewModel() {

    val gpuInfo: StateFlow<GPU> = hardwareRepository.getGpuStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GPU(),
        )
}
