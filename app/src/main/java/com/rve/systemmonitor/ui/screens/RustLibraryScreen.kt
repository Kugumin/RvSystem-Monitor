package com.rve.systemmonitor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rve.systemmonitor.R
import com.rve.systemmonitor.ui.components.ExitUntilCollapsedMediumTopAppBar
import com.rve.systemmonitor.utils.DeviceUtils
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import androidx.compose.runtime.Immutable

@Immutable
private data class JniMethod(val name: String, val returnType: String, val parameters: String = "env: JNIEnv", val description: String)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RustLibraryScreen(onNavigateBack: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val rustVersion = remember { DeviceUtils.getRustLibraryVersion() }

    val methods = remember {
        listOf(
            JniMethod(
                "getRustLibraryVersionNative",
                "jstring",
                description = "Returns the semantic version of the native library defined in Cargo.toml.",
            ),
            JniMethod(
                "getVulkanVersionNative",
                "jstring",
                description = "Queries the system Vulkan driver and returns formatted API and Driver versions.",
            ),
            JniMethod(
                "getGpuTemperatureNative",
                "jdouble",
                description = "Reads GPU thermal sensors via kernel sysfs nodes.",
            ),
            JniMethod(
                "getMemoryDataNative",
                "jdoubleArray",
                description = "Fetches a comprehensive batch of RAM and ZRAM metrics in a single JNI call.",
            ),
            JniMethod(
                "getRamDataNative",
                "jdoubleArray",
                description = "Returns detailed RAM statistics (Total, Available, Cached, etc.).",
            ),
            JniMethod(
                "getZramDataNative",
                "jdoubleArray",
                description = "Returns ZRAM specific metrics including compression ratio and swap usage.",
            ),
            JniMethod(
                "getAllCoreFrequenciesNative",
                "jlongArray",
                description = "Retrieves current clock speeds for all online CPU cores.",
            ),
            JniMethod(
                "getCoreCountNative",
                "jint",
                description = "Returns the total number of CPU cores detected by the kernel.",
            ),
            JniMethod(
                "getCoreFrequencyNative",
                "jlong",
                "env, core_id, type",
                "Returns specific frequency type (cur, min, max) for a given core ID.",
            ),
            JniMethod(
                "getCoreGovernorNative",
                "jstring",
                "env, core_id",
                "Returns the active CPU scaling governor for the specified core.",
            ),
            JniMethod(
                "getCpuTemperatureNative",
                "jdouble",
                description = "Retrieves the package/composite CPU temperature.",
            ),
            JniMethod(
                "getAllCoreTemperaturesNative",
                "jdoubleArray",
                description = "Returns an array of temperatures for each individual CPU core.",
            ),
            JniMethod(
                "getCpuDynamicDataNative",
                "jdoubleArray",
                description = "Batched retrieval of temperatures and frequencies for UI synchronization.",
            ),
        ).sortedBy { it.name }.toImmutableList()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ExitUntilCollapsedMediumTopAppBar(
                title = "Rust Library",
                onNavigateBack = onNavigateBack,
                scrollBehavior = scrollBehavior,
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 64.dp,
                start = 16.dp,
                end = 16.dp,
            ),
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 24.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_rust_logo),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    Column {
                        Text(
                            text = "librvsystem_monitor.so",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Version: v$rustVersion",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Text(
                    text = "Public methods",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }

            item {
                MethodSummaryTable(methods)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(
                    text = "Public method detail",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }

            items(methods) { method ->
                MethodDetailItem(method)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MethodSummaryTable(methods: ImmutableList<JniMethod>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)),
    ) {
        methods.forEachIndexed { index, method ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (index % 2 == 0) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(12.dp),
            ) {
                Text(
                    text = method.returnType,
                    modifier = Modifier.weight(0.3f),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = Color(0xFF37474F),
                )
                Text(
                    text = method.name,
                    modifier = Modifier.weight(0.7f),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (index < methods.lastIndex) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
private fun MethodDetailItem(method: JniMethod) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = method.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "${method.returnType} ${method.name} (${method.parameters})",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = method.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
