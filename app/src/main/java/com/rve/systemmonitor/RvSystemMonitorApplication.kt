package com.rve.systemmonitor

import android.app.Application
import com.rve.systemmonitor.shizuku.ShizukuManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RvSystemMonitorApplication : Application() {
    @Inject
    lateinit var shizukuManager: ShizukuManager

    override fun onCreate() {
        super.onCreate()
        shizukuManager.init()
    }
}
