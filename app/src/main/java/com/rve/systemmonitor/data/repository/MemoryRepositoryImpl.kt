package com.rve.systemmonitor.data.repository

import com.rve.systemmonitor.data.di.ApplicationScope
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class MemoryRepositoryImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @param:ApplicationScope private val externalScope: kotlinx.coroutines.CoroutineScope
) : MemoryRepository {
    private val TAG = "MemoryRepository"

    private val sharedMemoryStream by lazy {
        settingsRepository.memoryRefreshDelay.flatMapLatest { delayMillis ->
            FlowUtils.pollingFlow(TAG, delayMillis) {
                MemoryUtils.getMemoryInfo()
            }
        }.shareIn(
            scope = externalScope,
            started = SharingStarted.WhileSubscribed(5000),
            replay = 1,
        )
    }

    override fun getMemoryInfo(): Flow<Pair<RAM, ZRAM>> = sharedMemoryStream
}
