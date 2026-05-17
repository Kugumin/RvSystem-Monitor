package com.rve.systemmonitor.data.repository

import com.rve.systemmonitor.domain.model.CPU
import com.rve.systemmonitor.domain.model.CoreDetail
import com.rve.systemmonitor.domain.repository.CpuRepository
import com.rve.systemmonitor.domain.repository.SettingsRepository
import com.rve.systemmonitor.utils.CpuUtils
import com.rve.systemmonitor.utils.FlowUtils
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class CpuRepositoryImpl @Inject constructor(private val settingsRepository: SettingsRepository) : CpuRepository {
    private val TAG = "CpuRepository"

    override fun getCpuInfo(): CPU {
        return CPU(
            manufacturer = CpuUtils.getSocManufacturer(),
            model = CpuUtils.getSocModel(),
            cores = CpuUtils.getCoreCount(),
            hardware = CpuUtils.getHardware(),
            board = CpuUtils.getBoard(),
            architecture = CpuUtils.getArchitecture(),
            temperature = CpuUtils.getCpuTemperature(),
        )
    }

    override fun getCpuStream(): Flow<CPU> = settingsRepository.cpuRefreshDelay.flatMapLatest { delayMillis ->
        val manufacturer = CpuUtils.getSocManufacturer()
        val model = CpuUtils.getSocModel()
        val cores = CpuUtils.getCoreCount()
        val hardware = CpuUtils.getHardware()
        val board = CpuUtils.getBoard()
        val architecture = CpuUtils.getArchitecture()

        val staticCoreInfo = (0 until cores).map { i ->
            CoreStaticInfo(
                minFreqKhz = CpuUtils.getCoreFrequencyKhz(i, "min_info"),
                maxFreqKhz = CpuUtils.getCoreFrequencyKhz(i, "max_info"),
                governor = CpuUtils.getCoreGovernor(i),
            )
        }

        FlowUtils.pollingFlow(TAG, delayMillis) {
            val dynamicData = CpuUtils.getCpuDynamicData()
            val cpuTemperature = dynamicData.getOrElse(0) { 0.0 }
            val coreDetails = ArrayList<CoreDetail>(cores)

            for (i in 0 until cores) {
                val currentKhz = dynamicData.getOrElse(1 + i * 2) { 0.0 }.toLong()
                val currentTemp = dynamicData.getOrElse(2 + i * 2) { 0.0 }
                val static = staticCoreInfo[i]
                coreDetails.add(
                    CoreDetail(
                        id = i,
                        currentFreqKhz = currentKhz,
                        minFreqKhz = static.minFreqKhz,
                        maxFreqKhz = static.maxFreqKhz,
                        governor = static.governor,
                        temperature = currentTemp,
                    ),
                )
            }

            CPU(
                manufacturer = manufacturer,
                model = model,
                cores = cores,
                hardware = hardware,
                board = board,
                architecture = architecture,
                temperature = cpuTemperature,
                coreDetails = coreDetails.toImmutableList(),
            )
        }
    }

    private data class CoreStaticInfo(val minFreqKhz: Long, val maxFreqKhz: Long, val governor: String)
}
