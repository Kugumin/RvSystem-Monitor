package com.rve.systemmonitor.utils

import com.rve.systemmonitor.shizuku.ShizukuManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

@Singleton
class FpsMonitor @Inject constructor(
    private val shizukuManager: ShizukuManager,
    private val settingsRepository: com.rve.systemmonitor.domain.repository.SettingsRepository,
) {
    val framesPerSecond: Flow<Int> = flow {
        var initialized = false
        var lastKnownFps = 0

        while (true) {
            val useShizuku = settingsRepository.useShizuku.first()
            val shizukuReady = useShizuku &&
                shizukuManager.isShizukuAvailable.value &&
                shizukuManager.hasPermission.value

            if (shizukuReady) {
                try {
                    if (!initialized) {
                        shizukuManager.executeCommand(
                            "dumpsys SurfaceFlinger --timestats -clear -enable",
                        )
                        initialized = true
                    } else {
                        val output = shizukuManager.executeCommand(
                            "dumpsys SurfaceFlinger --timestats -dump",
                        )

                        val parsed = Regex("averageFPS\\s*=\\s*([0-9.]+)")
                            .find(output)
                            ?.groupValues?.get(1)
                            ?.toFloatOrNull()
                            ?.toInt()
                            ?: 0

                        if (parsed > 0) lastKnownFps = parsed
                        emit(lastKnownFps)

                        if (System.currentTimeMillis() % 3000L < 1000L) {
                            shizukuManager.executeCommand(
                                "dumpsys SurfaceFlinger --timestats -clear -enable",
                            )
                        }
                    }
                } catch (e: Exception) {
                    emit(lastKnownFps)
                }
            } else {
                initialized = false
                lastKnownFps = 0
                emit(0)
            }
            delay(1000)
        }
    }
}
