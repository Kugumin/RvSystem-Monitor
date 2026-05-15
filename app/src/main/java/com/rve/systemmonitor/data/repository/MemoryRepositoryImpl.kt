@file:OptIn(ExperimentalCoroutinesApi::class)

package com.rve.systemmonitor.data.repository

import com.rve.systemmonitor.domain.model.RAM
import com.rve.systemmonitor.domain.model.ZRAM
import com.rve.systemmonitor.domain.repository.MemoryRepository
import com.rve.systemmonitor.domain.repository.SettingsRepository
import com.rve.systemmonitor.utils.FlowUtils
import com.rve.systemmonitor.utils.MemoryUtils
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

@Singleton
class MemoryRepositoryImpl @Inject constructor(private val settingsRepository: SettingsRepository) : MemoryRepository {
    private val TAG = "MemoryRepository"

    override fun getMemoryInfo(): Flow<Pair<RAM, ZRAM>> = settingsRepository.memoryRefreshDelay.flatMapLatest { delayMillis ->
        FlowUtils.pollingFlow(TAG, delayMillis) {
            MemoryUtils.getMemoryInfo()
        }
    }
}
