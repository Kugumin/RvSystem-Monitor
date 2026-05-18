package com.rve.systemmonitor.utils

import android.os.Build
import android.util.Log

object DeviceUtils {
    const val TAG = "DeviceUtils"

    fun getManufacturer(): String = runCatching {
        Build.MANUFACTURER
    }.getOrElse {
        Log.e(TAG, "getManufacturer: ${it.message}", it)
        "unknown"
    }

    fun getModel(): String = runCatching {
        Build.MODEL
    }.getOrElse {
        Log.e(TAG, "getModel: ${it.message}", it)
        "unknown"
    }

    fun getDevice(): String = runCatching {
        Build.DEVICE
    }.getOrElse {
        Log.e(TAG, "getDevice: ${it.message}", it)
        "unknown"
    }

    fun getRustLibraryVersion(): String = runCatching {
        getRustLibraryVersionNative()
    }.getOrElse {
        Log.e(TAG, "getRustLibraryVersion: ${it.message}", it)
        "unknown"
    }

    private external fun getRustLibraryVersionNative(): String

    init {
        NativeLoader.load()
    }
}
