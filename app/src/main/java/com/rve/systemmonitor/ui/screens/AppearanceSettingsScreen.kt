package com.rve.systemmonitor.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rve.systemmonitor.R
import com.rve.systemmonitor.ui.components.ExitUntilCollapsedMediumTopAppBar
import com.rve.systemmonitor.ui.components.haptic.hapticClickable
import com.rve.systemmonitor.ui.components.haptic.rememberHapticOnClick
import com.rve.systemmonitor.ui.viewmodel.SettingsViewModel
import com.rve.systemmonitor.utils.ThemeMode
import com.rve.systemmonitor.utils.VibrationIntensity

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppearanceSettingsScreen(viewModel: SettingsViewModel = hiltViewModel(), onNavigateBack: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val currentTheme by viewModel.themeMode.collectAsStateWithLifecycle()
    val amoledMode by viewModel.amoledMode.collectAsStateWithLifecycle()
    val hapticEnabled by viewModel.hapticFeedbackEnabled.collectAsStateWithLifecycle()
    val vibrationIntensity by viewModel.vibrationIntensity.collectAsStateWithLifecycle()

    val darkTheme = when (currentTheme) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ExitUntilCollapsedMediumTopAppBar(
                title = "Appearance",
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
                ) {
                    Text(
                        text = "Visual Style",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp, start = 8.dp),
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.brightness_medium_filled),
                                        contentDescription = "Theme Icon",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                    )
                                }

                                Column {
                                    Text(
                                        text = "App Theme",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = "Choose your preferred visual style",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val themeOptions = listOf(
                                ThemeMode.LIGHT to "Light",
                                ThemeMode.SYSTEM to "System",
                                ThemeMode.DARK to "Dark",
                            )

                            SingleChoiceSegmentedButtonRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                            ) {
                                themeOptions.forEachIndexed { index, (mode, label) ->
                                    SegmentedButton(
                                        shape = SegmentedButtonDefaults.itemShape(
                                            index = index,
                                            count = themeOptions.size,
                                        ),
                                        onClick = rememberHapticOnClick { viewModel.setThemeMode(mode) },
                                        selected = currentTheme == mode,
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = if (currentTheme == mode) FontWeight.Bold else FontWeight.Normal,
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val amoledEnabled = darkTheme
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .hapticClickable(enabled = amoledEnabled) { viewModel.setAmoledMode(!amoledMode) }
                                    .padding(horizontal = 20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 8.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (amoledEnabled) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.night_mode_filled),
                                            contentDescription = "Amoled Icon",
                                            tint = if (amoledEnabled) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f),
                                        )                                    }

                                    Column {
                                        Text(
                                            text = "Amoled Mode",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (amoledEnabled) MaterialTheme.colorScheme.onSurface
                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                        )
                                        Text(
                                            text = if (amoledEnabled) "Pure black background for OLED screens"
                                            else "Dark mode is required to use Amoled mode",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (amoledEnabled) MaterialTheme.colorScheme.onSurfaceVariant
                                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                                        )
                                    }
                                }

                                Switch(
                                    enabled = amoledEnabled,
                                    checked = amoledMode && amoledEnabled,
                                    onCheckedChange = { viewModel.setAmoledMode(it) },
                                    thumbContent = {
                                        Crossfade(
                                            targetState = amoledMode && amoledEnabled,
                                            animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                                            label = "Amoled Switch Icon",
                                        ) { enabled ->
                                            Icon(
                                                painter = painterResource(
                                                    if (enabled) R.drawable.check_rounded else R.drawable.close_rounded,
                                                ),
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                            )
                                        }
                                    },
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .hapticClickable { viewModel.setHapticFeedbackEnabled(!hapticEnabled) }
                                    .padding(horizontal = 20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 8.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.mobile_vibrate_filled),
                                            contentDescription = "Haptic Icon",
                                            tint = MaterialTheme.colorScheme.onSecondary,
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "Haptic Feedback",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                        Text(
                                            text = "Subtle vibrations on interaction",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }

                                Switch(
                                    checked = hapticEnabled,
                                    onCheckedChange = { viewModel.setHapticFeedbackEnabled(it) },
                                    thumbContent = {
                                        Crossfade(
                                            targetState = hapticEnabled,
                                            animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                                            label = "Haptic Switch Icon",
                                        ) { enabled ->
                                            Icon(
                                                painter = painterResource(
                                                    if (enabled) R.drawable.check_rounded else R.drawable.close_rounded,
                                                ),
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                            )
                                        }
                                    },
                                )
                            }

                            AnimatedVisibility(
                                visible = hapticEnabled,
                                enter = expandVertically(
                                    animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                                ) + fadeIn(
                                    animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                                ),
                                exit = shrinkVertically(
                                    animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                                ) + fadeOut(
                                    animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                                ),
                            ) {
                                Column(
                                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
                                ) {
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Vibration Intensity",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 8.dp),
                                    )

                                    val intensityOptions = listOf(
                                        VibrationIntensity.LIGHT to "Light",
                                        VibrationIntensity.MEDIUM to "Medium",
                                        VibrationIntensity.STRONG to "Strong",
                                    )

                                    SingleChoiceSegmentedButtonRow(
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        intensityOptions.forEachIndexed { index, (intensity, label) ->
                                            SegmentedButton(
                                                shape = SegmentedButtonDefaults.itemShape(
                                                    index = index,
                                                    count = intensityOptions.size,
                                                ),
                                                onClick = rememberHapticOnClick { viewModel.setVibrationIntensity(intensity) },
                                                selected = vibrationIntensity == intensity,
                                            ) {
                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    fontWeight = if (vibrationIntensity ==
                                                        intensity
                                                    ) FontWeight.Bold else FontWeight.Normal,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
