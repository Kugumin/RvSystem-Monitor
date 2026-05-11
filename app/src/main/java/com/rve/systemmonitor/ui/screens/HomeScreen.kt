package com.rve.systemmonitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rve.systemmonitor.R
import com.rve.systemmonitor.ui.components.card.InfoCardData
import com.rve.systemmonitor.ui.components.card.InfoOverviewCard
import com.rve.systemmonitor.ui.components.dialog.HelpBottomSheetContent
import com.rve.systemmonitor.ui.components.layout.ScreenLazyColumn
import com.rve.systemmonitor.ui.utils.rememberLifecycleAwareState
import com.rve.systemmonitor.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(isActive: Boolean, viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by rememberLifecycleAwareState(isActive, viewModel.uiState)

    var showHelpSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showHelpSheet) {
        ModalBottomSheet(
            onDismissRequest = { showHelpSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            HomeHelpContent()
        }
    }

    val infoCards = remember(uiState) {
        listOf(
            InfoCardData(
                title = "Device",
                headline = uiState.device.model,
                subhead = "by ${uiState.device.manufacturer}",
                iconRes = R.drawable.mobile_filled,
                badges = listOf(uiState.device.device),
                onHelpClick = { showHelpSheet = true },
            ),
            InfoCardData(
                title = "Operating System",
                headline = "${uiState.os.name} ${uiState.os.version}",
                subhead = uiState.os.dessertName,
                iconRes = R.drawable.android_filled,
                backgroundIconOffset = 45.dp,
                badges = listOf("API ${uiState.os.sdk}", "Patch: ${uiState.os.securityPatch}"),
            ),
            InfoCardData(
                title = "Display",
                headline = uiState.display.resolution,
                subhead = "${uiState.display.screenSizeInches}\" Screen Size",
                iconRes = R.drawable.mobile_3_filled,
                backgroundIconOffset = 20.dp,
                badges = buildList {
                    add("${uiState.display.refreshRate}Hz")
                    add("${uiState.display.densityDpi} dpi")
                    if (uiState.display.isHdrSupported) {
                        add("HDR")
                        addAll(uiState.display.hdrTypes)
                    }
                },
                secondaryBadgeIndices = if (uiState.display.isHdrSupported) listOf(0, 1) else listOf(0),
            ),
            InfoCardData(
                title = "Processor",
                headline = uiState.cpu.model,
                subhead = "by ${uiState.cpu.manufacturer}",
                iconRes = R.drawable.memory_filled,
                badges = listOf("${uiState.cpu.cores} Cores"),
            ),
            InfoCardData(
                title = "Graphics",
                headline = uiState.gpu.renderer,
                subhead = "by ${uiState.gpu.vendor}",
                iconRes = R.drawable.view_in_ar_filled,
                badges = listOf(
                    "OpenGL ES ${uiState.gpu.glesVersion}",
                    "Vulkan ${uiState.gpu.vulkanVersion}",
                ),
            ),
        )
    }

    ScreenLazyColumn {
        items(
            items = infoCards,
            key = { it.title },
        ) { cardData ->
            InfoOverviewCard(data = cardData)
        }
    }
}

@Composable
private fun HomeHelpContent() {
    val helpItems = listOf(
        "Device & Operating System" to "Information such as model, manufacturer, and Android version is extracted from " +
            "the system's Build properties and secure patch levels.",
        "Display" to "Screen resolution, refresh rate, and density metrics are obtained via the " +
            "Android WindowManager and Display APIs.",
        "Processor (CPU)" to "Detailed hardware info, including core count and architecture, is parsed from " +
            "Linux kernel files (/proc/cpuinfo) using the high-performance Rust backend.",
        "Graphics (GPU)" to "The graphics renderer, vendor, and OpenGL ES version are retrieved directly " +
            "from the device's GPU through the EGL context.",
    )

    HelpBottomSheetContent(helpItems = helpItems)
}
