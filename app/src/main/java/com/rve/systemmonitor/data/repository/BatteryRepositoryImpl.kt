@file:OptIn(ExperimentalCoroutinesApi::class)

package com.rve.systemmonitor.data.repository

import android.app.Application
import android.os.SystemClock
import com.rve.systemmonitor.R
import com.rve.systemmonitor.data.di.ApplicationScope
import com.rve.systemmonitor.domain.model.Battery
import com.rve.systemmonitor.domain.repository.BatteryRepository
import com.rve.systemmonitor.domain.repository.SettingsRepository
import com.rve.systemmonitor.utils.BatteryUtils
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class BatteryRepositoryImpl @Inject constructor(
    private val application: Application,
    private val settingsRepository: SettingsRepository,
    @param:ApplicationScope private val externalScope: CoroutineScope,
) : BatteryRepository {

    private val staticBatteryInfo by lazy {
        val intent = BatteryUtils.getBatteryIntent(application)
        val deepSleep = SystemClock.elapsedRealtime() - SystemClock.uptimeMillis()
        if (intent != null) {
            val design = BatteryUtils.getCapacity(application)
            BatteryInfo(
                healthRes = BatteryUtils.getHealthRes(intent),
                technology = BatteryUtils.getTechnology(intent),
                designCapacity = design,
                deepSleep = deepSleep,
            )
        } else {
            BatteryInfo(R.string.value_unknown, "Unknown", -1.0, deepSleep)
        }
    }

    private val sharedBatteryStream by lazy {
        val broadcastFlow = BatteryUtils.getBatteryFlow(application)

        val pollingFlow = settingsRepository.batteryRefreshDelay.flatMapLatest { delayMillis ->
            flow {
                delay(400) // Initial delay to avoid startup peak
                while (true) {
                    emit(BatteryUtils.getCurrent(application))
                    delay(delayMillis)
                }
            }
        }

        combine(broadcastFlow, pollingFlow) { intent, currentNow ->
            val voltage = BatteryUtils.getVoltage(intent)
            val level = BatteryUtils.getLevel(intent)

            Battery(
                level = level,
                healthRes = staticBatteryInfo.healthRes,
                status = if (level == 100 && BatteryUtils.getStatusRes(intent) == R.string.battery_status_charging) {
                    "Full"
                } else {
                    "Unknown"
                }, // Simplified status for internal logic
                statusRes = BatteryUtils.getStatusRes(intent),
                technology = staticBatteryInfo.technology,
                voltage = voltage,
                temperature = BatteryUtils.getTemperature(intent),
                capacity = staticBatteryInfo.designCapacity,
                cycleCount = BatteryUtils.getCycleCount(intent),
                uptime = SystemClock.elapsedRealtime(),
                deepSleep = staticBatteryInfo.deepSleep,
                current = currentNow,
                wattage = calculateWattage(currentNow, voltage),
                powerSourceRes = BatteryUtils.getPowerSourceRes(intent),
            )
        }.flowOn(Dispatchers.IO)
            .shareIn(
                scope = externalScope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1,
            )
    }

    private data class BatteryInfo(val healthRes: Int, val technology: String, val designCapacity: Double, val deepSleep: Long)

    override fun getBatteryInfo(): Battery {
        val intent = BatteryUtils.getBatteryIntent(application)
        return if (intent != null) {
            val current = BatteryUtils.getCurrent(application)
            val voltage = BatteryUtils.getVoltage(intent)
            val level = BatteryUtils.getLevel(intent)

            Battery(
                level = level,
                healthRes = staticBatteryInfo.healthRes,
                statusRes = BatteryUtils.getStatusRes(intent),
                technology = staticBatteryInfo.technology,
                voltage = voltage,
                temperature = BatteryUtils.getTemperature(intent),
                capacity = staticBatteryInfo.designCapacity,
                cycleCount = BatteryUtils.getCycleCount(intent),
                uptime = SystemClock.elapsedRealtime(),
                deepSleep = staticBatteryInfo.deepSleep,
                current = current,
                wattage = calculateWattage(current, voltage),
                powerSourceRes = BatteryUtils.getPowerSourceRes(intent),
            )
        } else {
            Battery()
        }
    }

    override fun getBatteryStream(): Flow<Battery> = sharedBatteryStream

    private fun calculateWattage(currentMA: Int, voltageMV: Int): Double {
        return (abs(currentMA) / 1000.0) * (voltageMV / 1000.0)
    }
}
