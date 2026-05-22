package com.rve.systemmonitor.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Choreographer
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.rve.systemmonitor.R
import com.rve.systemmonitor.domain.repository.OverlayRepository
import com.rve.systemmonitor.utils.BatteryUtils
import com.rve.systemmonitor.utils.CpuUtils
import com.rve.systemmonitor.utils.MemoryUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SystemOverlayService : Service() {

    @Inject
    lateinit var overlayRepository: OverlayRepository

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var metricsTextView: TextView? = null

    private var showFps = true
    private var showRamPercentage = false
    private var showRamGb = false
    private var showBatteryTemp = false
    private var showCpuTemp = false
    private var overlayTextSize = 14f
    private var overlayBgOpacity = 0.5f
    private var overlayPadding = 16
    private var overlayTextColor = Color.GREEN
    private var isVerticalLayout = false
    private var overlayCornerRadius = 8

    private val choreographer = Choreographer.getInstance()
    private var lastFrameTimeNanos: Long = 0
    private var frameCount = 0
    private var lastFpsUpdateTime: Long = 0
    private var updateDelayNanos: Long = 1_000_000_000L // Default 1s

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)

    @Volatile private var ramText: String = ""
    @Volatile private var cpuText: String = ""
    @Volatile private var batteryText: String = ""
    @Volatile private var lastCalculatedFps: Long = 0L

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (lastFrameTimeNanos != 0L) {
                frameCount++
            }

            val elapsedNanos = frameTimeNanos - lastFpsUpdateTime
            if (lastFpsUpdateTime == 0L || elapsedNanos >= updateDelayNanos) {
                if (lastFpsUpdateTime != 0L && elapsedNanos > 0) {
                    lastCalculatedFps = (frameCount * 1_000_000_000L) / elapsedNanos
                }
                updateMetricsDisplay()
                frameCount = 0
                lastFpsUpdateTime = frameTimeNanos
            }

            lastFrameTimeNanos = frameTimeNanos
            choreographer.postFrameCallback(this)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntent(intent)
        return START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            showFps = it.getBooleanExtra("show_fps", showFps)
            showRamPercentage = it.getBooleanExtra("show_ram_percentage", showRamPercentage)
            showRamGb = it.getBooleanExtra("show_ram_gb", showRamGb)
            showBatteryTemp = it.getBooleanExtra("show_battery_temp", showBatteryTemp)
            showCpuTemp = it.getBooleanExtra("show_cpu_temp", showCpuTemp)

            val interval = it.getLongExtra("update_delay", -1L)
            if (interval != -1L) {
                updateDelayNanos = interval * 1_000_000L
            }

            overlayTextSize = it.getFloatExtra("text_size", overlayTextSize)
            overlayBgOpacity = it.getFloatExtra("bg_opacity", overlayBgOpacity)
            overlayPadding = it.getIntExtra("padding", overlayPadding)
            overlayTextColor = it.getIntExtra("text_color", overlayTextColor)
            isVerticalLayout = it.getBooleanExtra("is_vertical", isVerticalLayout)
            overlayCornerRadius = it.getIntExtra("corner_radius", overlayCornerRadius)

            applySettings()
        }
    }

    private fun applySettings() {
        metricsTextView?.apply {
            textSize = overlayTextSize
            setTextColor(overlayTextColor)
            val alphaInt = (overlayBgOpacity * 255).toInt()

            val shape = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.argb(alphaInt, 0, 0, 0))
                cornerRadius = overlayCornerRadius.toFloat()
            }
            background = shape

            setPadding(overlayPadding, overlayPadding / 2, overlayPadding, overlayPadding / 2)
        }
        updateMetricsDisplay()
    }

    private fun updateMetricsDisplay() {
        serviceScope.launch(Dispatchers.Main) {
            val metrics = mutableListOf<String>()

            if (showFps) {
                metrics.add(getString(R.string.overlay_format_fps, lastCalculatedFps))
            }

            val currentRam = ramText
            if ((showRamGb || showRamPercentage) && currentRam.isNotEmpty()) {
                metrics.add(currentRam)
            }

            val currentBattery = batteryText
            if (showBatteryTemp && currentBattery.isNotEmpty()) {
                metrics.add(currentBattery)
            }

            val currentCpu = cpuText
            if (showCpuTemp && currentCpu.isNotEmpty()) {
                metrics.add(currentCpu)
            }

            val separator = if (isVerticalLayout) "\n" else " | "
            val newText = metrics.joinToString(separator)
            if (metricsTextView?.text != newText) {
                metricsTextView?.text = newText
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        startForeground(NOTIFICATION_ID, createNotification())
        showOverlay()

        // Initial hardware data
        refreshHardwareMetrics()
        lastBatteryIntent = BatteryUtils.getBatteryIntent(this)
        updateBatteryText()

        startSettingsObservation()
        startMetricsPolling()
        startBatteryMonitoring()
        choreographer.postFrameCallback(frameCallback)
    }

    private fun startSettingsObservation() {
        serviceScope.launch {
            overlayRepository.isFpsEnabled.collect {
                showFps = it
                updateMetricsDisplay()
            }
        }
        serviceScope.launch {
            overlayRepository.isRamPercentageEnabled.collect {
                showRamPercentage = it
                refreshHardwareMetrics()
                updateMetricsDisplay()
            }
        }
        serviceScope.launch {
            overlayRepository.isRamGbEnabled.collect {
                showRamGb = it
                refreshHardwareMetrics()
                updateMetricsDisplay()
            }
        }
        serviceScope.launch {
            overlayRepository.isBatteryTempEnabled.collect {
                showBatteryTemp = it
                updateBatteryText()
                updateMetricsDisplay()
            }
        }
        serviceScope.launch {
            overlayRepository.isCpuTempEnabled.collect {
                showCpuTemp = it
                refreshHardwareMetrics()
                updateMetricsDisplay()
            }
        }
        serviceScope.launch {
            overlayRepository.overlayUpdateInterval.collect { delayMillis ->
                updateDelayNanos = delayMillis * 1_000_000L
                updateMetricsDisplay()
            }
        }
        serviceScope.launch(Dispatchers.Main) {
            overlayRepository.overlayTextSize.collect {
                overlayTextSize = it
                applySettings()
            }
        }
        serviceScope.launch(Dispatchers.Main) {
            overlayRepository.overlayBgOpacity.collect {
                overlayBgOpacity = it
                applySettings()
            }
        }
        serviceScope.launch(Dispatchers.Main) {
            overlayRepository.overlayPadding.collect {
                overlayPadding = it
                applySettings()
            }
        }
        serviceScope.launch(Dispatchers.Main) {
            overlayRepository.overlayTextColor.collect {
                overlayTextColor = it
                applySettings()
            }
        }
        serviceScope.launch(Dispatchers.Main) {
            overlayRepository.isVerticalLayout.collect {
                isVerticalLayout = it
                applySettings()
            }
        }
        serviceScope.launch(Dispatchers.Main) {
            overlayRepository.overlayCornerRadius.collect {
                overlayCornerRadius = it
                applySettings()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.cancel()
        choreographer.removeFrameCallback(frameCallback)
        if (overlayView != null) {
            windowManager?.removeView(overlayView)
        }
    }

    private fun startMetricsPolling() {
        serviceScope.launch {
            while (isActive) {
                refreshHardwareMetrics()
                val delayMillis = updateDelayNanos / 1_000_000L
                delay(if (delayMillis > 0) delayMillis else 1000L)
            }
        }
    }

    private fun refreshHardwareMetrics() {
        if (showRamGb || showRamPercentage) {
            val ram = MemoryUtils.getRamData()
            ramText = when {
                showRamGb && showRamPercentage -> {
                    getString(R.string.overlay_format_ram_gb_percent, ram.used, ram.total, ram.usedPercentage)
                }

                showRamGb -> {
                    getString(R.string.overlay_format_ram_gb, ram.used, ram.total)
                }

                showRamPercentage -> {
                    getString(R.string.overlay_format_ram_percent, ram.usedPercentage)
                }

                else -> {
                    getString(R.string.overlay_format_ram_gb_percent, ram.used, ram.total, ram.usedPercentage)
                }
            }
        } else {
            ramText = ""
        }

        if (showCpuTemp) {
            val cpuData = CpuUtils.getCpuDynamicData()
            if (cpuData.isNotEmpty()) {
                val temp = cpuData[0] // overall_temp is at index 0
                cpuText = getString(R.string.overlay_format_cpu_temp, temp)
            }
        } else {
            cpuText = ""
        }
    }

    private var lastBatteryIntent: Intent? = null

    private fun startBatteryMonitoring() {
        serviceScope.launch {
            BatteryUtils.getBatteryFlow(this@SystemOverlayService).collect { intent ->
                lastBatteryIntent = intent
                updateBatteryText()
            }
        }
    }

    private fun updateBatteryText() {
        val intent = lastBatteryIntent
        if (showBatteryTemp && intent != null) {
            val temp = BatteryUtils.getTemperature(intent)
            batteryText = getString(R.string.overlay_format_battery_temp, temp)
        } else {
            batteryText = ""
        }
    }

    private fun showOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        val textView = TextView(this).apply {
            text = ""
            textSize = overlayTextSize
            setTextColor(overlayTextColor)

            val alphaInt = (overlayBgOpacity * 255).toInt()
            val shape = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.argb(alphaInt, 0, 0, 0))
                cornerRadius = overlayCornerRadius.toFloat()
            }
            background = shape

            setPadding(overlayPadding, overlayPadding / 2, overlayPadding, overlayPadding / 2)
        }

        textView.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(v, params)
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        v.performClick()
                        return true
                    }
                }
                return false
            }
        })

        metricsTextView = textView
        overlayView = textView
        windowManager?.addView(overlayView, params)
    }

    private fun createNotification(): Notification {
        val channelId = "system_overlay_channel"
        val channel = NotificationChannel(
            channelId,
            getString(R.string.notification_channel_overlay),
            NotificationManager.IMPORTANCE_LOW,
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.notification_title_overlay_active))
            .setContentText(getString(R.string.notification_text_overlay))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        var isRunning = false
    }
}
