package com.rve.systemmonitor.utils

import android.util.Log

object NativeLoader {
    private const val TAG = "NativeLoader"
    private var isLoaded = false

    fun load() {
        if (isLoaded) return
        try {
            System.loadLibrary("rvsystem_monitor")
            isLoaded = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load Rust library: ${e.message}", e)
        }
    }
}
