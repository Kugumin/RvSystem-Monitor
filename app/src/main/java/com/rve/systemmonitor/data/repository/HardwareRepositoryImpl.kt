package com.rve.systemmonitor.data.repository

import android.app.Application
import com.rve.systemmonitor.domain.model.Device
import com.rve.systemmonitor.domain.model.Display
import com.rve.systemmonitor.domain.model.GPU
import com.rve.systemmonitor.domain.model.OS
import com.rve.systemmonitor.domain.model.Storage
import com.rve.systemmonitor.domain.repository.HardwareRepository
import com.rve.systemmonitor.utils.DeviceUtils
import com.rve.systemmonitor.utils.DisplayUtils
import com.rve.systemmonitor.utils.GpuUtils
import com.rve.systemmonitor.utils.OSUtils
import com.rve.systemmonitor.utils.StorageUtils
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.collections.immutable.toImmutableList

@Singleton
class HardwareRepositoryImpl @Inject constructor(private val application: Application) : HardwareRepository {

    private val device by lazy {
        Device(
            manufacturer = DeviceUtils.getManufacturer(),
            model = DeviceUtils.getModel(),
            device = DeviceUtils.getDevice(),
        )
    }

    private val os by lazy {
        val currentSdk = OSUtils.getSdkInt()
        OS(
            version = OSUtils.getAndroidVersion(),
            sdk = currentSdk,
            dessertName = "unknown", // We will use dessertNameRes for display
            dessertNameRes = OSUtils.getDessertNameRes(currentSdk),
            securityPatch = OSUtils.getSecurityPatch(),
        )
    }

    private val display by lazy {
        val (isHdr, hdrTypes) = DisplayUtils.getHdrCapabilities(application)
        Display(
            resolution = DisplayUtils.getResolution(application),
            refreshRate = DisplayUtils.getRefreshRate(application),
            densityDpi = DisplayUtils.getDensityDpi(application),
            screenSizeInches = DisplayUtils.getScreenSizeInches(application),
            isHdrSupported = isHdr,
            hdrTypes = hdrTypes.toImmutableList(),
        )
    }

    private val gpu by lazy {
        val (renderer, vendor, caps) = GpuUtils.getGpuDetails()
        val (maxTexSize, extCount) = caps
        GPU(
            renderer = renderer,
            vendor = vendor,
            glesVersion = GpuUtils.getGlesVersion(application),
            detailedGlesVersion = GpuUtils.getDetailedGlesVersion(),
            vulkanVersion = GpuUtils.getVulkanVersion(application),
            vulkanDriverVersion = GpuUtils.getVulkanDriverVersion(),
            temperature = GpuUtils.getGpuTemperature(),
            maxTextureSize = maxTexSize,
            extensionsCount = extCount,
            deviceType = GpuUtils.getVulkanDeviceType(),
            shadingLanguageVersion = GpuUtils.getShadingLanguageVersion(),
            totalMemoryMb = GpuUtils.getGpuMemoryInfo(application),
        )
    }

    private val storage by lazy {
        StorageUtils.getStorageData()
    }

    override fun getDeviceInfo(): Device = device

    override fun getOSInfo(): OS = os

    override fun getDisplayInfo(): Display = display

    override fun getGpuInfo(): GPU = gpu

    override fun getStorageInfo(): Storage = storage
}
