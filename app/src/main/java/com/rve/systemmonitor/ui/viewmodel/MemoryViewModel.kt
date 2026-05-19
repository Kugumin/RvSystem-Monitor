package com.rve.systemmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rve.systemmonitor.domain.model.RAM
import com.rve.systemmonitor.domain.model.Storage
import com.rve.systemmonitor.domain.model.ZRAM
import com.rve.systemmonitor.domain.repository.HardwareRepository
import com.rve.systemmonitor.domain.repository.MemoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MemoryViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val hardwareRepository: HardwareRepository,
) : ViewModel() {
    private val storageInfo = MutableStateFlow(Storage())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            storageInfo.value = hardwareRepository.getStorageInfo()
        }
    }

    private val memoryStream = memoryRepository.getMemoryInfo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RAM() to ZRAM(),
        )

    private val staticStorage = storageInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Storage(),
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
        viewModelScope.launch(Dispatchers.IO) {
            storageInfo.value = hardwareRepository.getStorageInfo()
        }
    }
}
