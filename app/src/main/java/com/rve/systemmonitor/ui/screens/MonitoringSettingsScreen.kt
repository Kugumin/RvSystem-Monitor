package com.rve.systemmonitor.ui.screens

import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rve.systemmonitor.R
import com.rve.systemmonitor.ui.components.ExitUntilCollapsedMediumTopAppBar
import com.rve.systemmonitor.ui.components.card.SettingsSliderCard
import com.rve.systemmonitor.ui.components.haptic.rememberHapticOnValueChange
import com.rve.systemmonitor.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MonitoringSettingsScreen(viewModel: SettingsViewModel = hiltViewModel(), onNavigateBack: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val cpuDelayMillis by viewModel.cpuRefreshDelay.collectAsStateWithLifecycle()
    val memoryDelayMillis by viewModel.memoryRefreshDelay.collectAsStateWithLifecycle()
    val gpuDelayMillis by viewModel.gpuRefreshDelay.collectAsStateWithLifecycle()
    val batteryDelayMillis by viewModel.batteryRefreshDelay.collectAsStateWithLifecycle()
    val batteryGraphHistorySeconds by viewModel.batteryGraphHistorySeconds.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val snapAnimationSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()

    val cpuSliderState = rememberSliderState(
        value = (cpuDelayMillis / 1000).toFloat(),
        steps = 3,
        valueRange = 1f..5f,
    )
    var cpuCurrentValue by rememberSaveable(cpuDelayMillis) { mutableFloatStateOf((cpuDelayMillis / 1000).toFloat()) }
    var cpuAnimateJob: Job? by remember { mutableStateOf(null) }

    val memorySliderState = rememberSliderState(
        value = (memoryDelayMillis / 1000).toFloat(),
        steps = 3,
        valueRange = 1f..5f,
    )
    var memoryCurrentValue by rememberSaveable(memoryDelayMillis) { mutableFloatStateOf((memoryDelayMillis / 1000).toFloat()) }
    var memoryAnimateJob: Job? by remember { mutableStateOf(null) }

    val gpuSliderState = rememberSliderState(
        value = (gpuDelayMillis / 1000).toFloat(),
        steps = 3,
        valueRange = 1f..5f,
    )
    var gpuCurrentValue by rememberSaveable(gpuDelayMillis) { mutableFloatStateOf((gpuDelayMillis / 1000).toFloat()) }
    var gpuAnimateJob: Job? by remember { mutableStateOf(null) }

    val batterySliderState = rememberSliderState(
        value = (batteryDelayMillis / 1000).toFloat(),
        steps = 3,
        valueRange = 1f..5f,
    )
    var batteryCurrentValue by rememberSaveable(batteryDelayMillis) { mutableFloatStateOf((batteryDelayMillis / 1000).toFloat()) }
    var batteryAnimateJob: Job? by remember { mutableStateOf(null) }

    val historySliderState = rememberSliderState(
        value = batteryGraphHistorySeconds.toFloat().coerceIn(30f, 300f),
        steps = 8,
        valueRange = 30f..300f,
    )
    var historyCurrentValue by rememberSaveable(batteryGraphHistorySeconds) {
        mutableFloatStateOf(batteryGraphHistorySeconds.toFloat().coerceIn(30f, 300f))
    }
    var historyAnimateJob: Job? by remember { mutableStateOf(null) }

    LaunchedEffect(cpuDelayMillis) {
        if (!cpuSliderState.isDragging) {
            cpuSliderState.value = (cpuDelayMillis / 1000).toFloat()
            cpuCurrentValue = (cpuDelayMillis / 1000).toFloat()
        }
    }

    LaunchedEffect(memoryDelayMillis) {
        if (!memorySliderState.isDragging) {
            memorySliderState.value = (memoryDelayMillis / 1000).toFloat()
            memoryCurrentValue = (memoryDelayMillis / 1000).toFloat()
        }
    }

    LaunchedEffect(gpuDelayMillis) {
        if (!gpuSliderState.isDragging) {
            gpuSliderState.value = (gpuDelayMillis / 1000).toFloat()
            gpuCurrentValue = (gpuDelayMillis / 1000).toFloat()
        }
    }

    LaunchedEffect(batteryDelayMillis) {
        if (!batterySliderState.isDragging) {
            batterySliderState.value = (batteryDelayMillis / 1000).toFloat()
            batteryCurrentValue = (batteryDelayMillis / 1000).toFloat()
        }
    }

    LaunchedEffect(batteryGraphHistorySeconds) {
        if (!historySliderState.isDragging) {
            val coercedValue = batteryGraphHistorySeconds.toFloat().coerceIn(30f, 300f)
            historySliderState.value = coercedValue
            historyCurrentValue = coercedValue
        }
    }

    cpuSliderState.shouldAutoSnap = false
    cpuSliderState.onValueChange = rememberHapticOnValueChange { newValue ->
        cpuCurrentValue = newValue
        if (cpuSliderState.isDragging) {
            cpuAnimateJob?.cancel()
            cpuSliderState.value = newValue
        }
    }

    cpuSliderState.onValueChangeFinished = {
        cpuAnimateJob = coroutineScope.launch {
            animate(
                initialValue = cpuSliderState.value,
                targetValue = cpuCurrentValue,
                animationSpec = snapAnimationSpec,
            ) { value, _ ->
                cpuSliderState.value = value
            }
            viewModel.setCpuRefreshDelay(cpuCurrentValue.toLong() * 1000)
        }
    }

    memorySliderState.shouldAutoSnap = false
    memorySliderState.onValueChange = rememberHapticOnValueChange { newValue ->
        memoryCurrentValue = newValue
        if (memorySliderState.isDragging) {
            memoryAnimateJob?.cancel()
            memorySliderState.value = newValue
        }
    }

    memorySliderState.onValueChangeFinished = {
        memoryAnimateJob = coroutineScope.launch {
            animate(
                initialValue = memorySliderState.value,
                targetValue = memoryCurrentValue,
                animationSpec = snapAnimationSpec,
            ) { value, _ ->
                memorySliderState.value = value
            }
            viewModel.setMemoryRefreshDelay(memoryCurrentValue.toLong() * 1000)
        }
    }

    gpuSliderState.shouldAutoSnap = false
    gpuSliderState.onValueChange = rememberHapticOnValueChange { newValue ->
        gpuCurrentValue = newValue
        if (gpuSliderState.isDragging) {
            gpuAnimateJob?.cancel()
            gpuSliderState.value = newValue
        }
    }

    gpuSliderState.onValueChangeFinished = {
        gpuAnimateJob = coroutineScope.launch {
            animate(
                initialValue = gpuSliderState.value,
                targetValue = gpuCurrentValue,
                animationSpec = snapAnimationSpec,
            ) { value, _ ->
                gpuSliderState.value = value
            }
            viewModel.setGpuRefreshDelay(gpuCurrentValue.toLong() * 1000)
        }
    }

    batterySliderState.shouldAutoSnap = false
    batterySliderState.onValueChange = rememberHapticOnValueChange { newValue ->
        batteryCurrentValue = newValue
        if (batterySliderState.isDragging) {
            batteryAnimateJob?.cancel()
            batterySliderState.value = newValue
        }
    }

    batterySliderState.onValueChangeFinished = {
        batteryAnimateJob = coroutineScope.launch {
            animate(
                initialValue = batterySliderState.value,
                targetValue = batteryCurrentValue,
                animationSpec = snapAnimationSpec,
            ) { value, _ ->
                batterySliderState.value = value
            }
            viewModel.setBatteryRefreshDelay(batteryCurrentValue.toLong() * 1000)
        }
    }

    historySliderState.shouldAutoSnap = false
    historySliderState.onValueChange = rememberHapticOnValueChange { newValue ->
        historyCurrentValue = newValue
        if (historySliderState.isDragging) {
            historyAnimateJob?.cancel()
            historySliderState.value = newValue
        }
    }

    historySliderState.onValueChangeFinished = {
        historyAnimateJob = coroutineScope.launch {
            animate(
                initialValue = historySliderState.value,
                targetValue = historyCurrentValue,
                animationSpec = snapAnimationSpec,
            ) { value, _ ->
                historySliderState.value = value
            }
            viewModel.setBatteryGraphHistorySeconds(historyCurrentValue.toInt())
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ExitUntilCollapsedMediumTopAppBar(
                title = "Monitoring",
                onNavigateBack = onNavigateBack,
                scrollBehavior = scrollBehavior,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Refresh Intervals",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp),
                    )

                    SettingsSliderCard(
                        title = "CPU Update Interval",
                        description = "Adjust how often CPU stats are refreshed",
                        iconRes = R.drawable.memory_filled,
                        sliderState = cpuSliderState,
                        currentDisplayValue = cpuCurrentValue,
                        displayValueFormatter = { "${it.toInt()}s" },
                        onReset = { viewModel.setCpuRefreshDelay(3000L) },
                        isResetVisible = cpuCurrentValue != 3.0f,
                    )

                    SettingsSliderCard(
                        title = "GPU Update Interval",
                        description = "Adjust how often GPU stats are refreshed",
                        iconRes = R.drawable.view_in_ar_filled,
                        sliderState = gpuSliderState,
                        currentDisplayValue = gpuCurrentValue,
                        displayValueFormatter = { "${it.toInt()}s" },
                        onReset = { viewModel.setGpuRefreshDelay(3000L) },
                        isResetVisible = gpuCurrentValue != 3.0f,
                    )

                    SettingsSliderCard(
                        title = "Memory Update Interval",
                        description = "Adjust how often Memory stats are refreshed",
                        iconRes = R.drawable.memory_alt_filled,
                        sliderState = memorySliderState,
                        currentDisplayValue = memoryCurrentValue,
                        displayValueFormatter = { "${it.toInt()}s" },
                        onReset = { viewModel.setMemoryRefreshDelay(3000L) },
                        isResetVisible = memoryCurrentValue != 3.0f,
                    )

                    SettingsSliderCard(
                        title = "Battery Update Interval",
                        description = "Adjust how often uptime and current (mA) are refreshed",
                        iconRes = R.drawable.battery_android_full,
                        sliderState = batterySliderState,
                        currentDisplayValue = batteryCurrentValue,
                        displayValueFormatter = { "${it.toInt()}s" },
                        onReset = { viewModel.setBatteryRefreshDelay(1000L) },
                        isResetVisible = batteryCurrentValue != 1.0f,
                    )

                    SettingsSliderCard(
                        title = "Battery Graph History",
                        description = "Set how much data to show on the graph",
                        iconRes = R.drawable.timeline_rounded,
                        sliderState = historySliderState,
                        currentDisplayValue = historyCurrentValue,
                        displayValueFormatter = { value ->
                            if (value >= 60) {
                                val minutes = value.toInt() / 60
                                val seconds = value.toInt() % 60
                                if (seconds == 0) "${minutes}m" else "${minutes}m ${seconds}s"
                            } else {
                                "${value.toInt()}s"
                            }
                        },
                        onReset = { viewModel.setBatteryGraphHistorySeconds(60) },
                        isResetVisible = historyCurrentValue != 60.0f,
                        valueLabel = "History Duration",
                    )
                }
            }
        }
    }
}
