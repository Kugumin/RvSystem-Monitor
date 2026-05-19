package com.rve.systemmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rve.systemmonitor.domain.repository.HardwareRepository
import com.rve.systemmonitor.domain.repository.MemoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MemoryViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val hardwareRepository: HardwareRepository,
) : ViewModel() {
    private val storageInfo = kotlinx.coroutines.flow.MutableStateFlow(com.rve.systemmonitor.domain.model.Storage())

    init {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            storageInfo.value = hardwareRepository.getStorageInfo()
        }
    }

    private val memoryStream = memoryRepository.getMemoryInfo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = com.rve.systemmonitor.domain.model.RAM() to com.rve.systemmonitor.domain.model.ZRAM(),
        )

    private val staticStorage = storageInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = com.rve.systemmonitor.domain.model.Storage(),
        )

    val uiState = combine(
        memoryStream,
        staticStorage,
    ) { (ram, zram), storage ->
        MemoryUiState(
            ram = ram,
            zram = zram,
            storage = storage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MemoryUiState(),
    )

    fun refreshStorage() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            storageInfo.value = hardwareRepository.getStorageInfo()
        }
    }
}
