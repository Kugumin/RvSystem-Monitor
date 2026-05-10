package com.rve.systemmonitor.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rve.systemmonitor.R
import com.rve.systemmonitor.ui.components.haptic.hapticClickable
import com.rve.systemmonitor.ui.components.haptic.rememberHapticOnClick
import com.rve.systemmonitor.ui.viewmodel.SetupViewModel

private enum class SetupStep {
    OverlayPermission,
    Updates,
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SetupScreen(viewModel: SetupViewModel = hiltViewModel(), onSetupCompleted: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val autoUpdateEnabled by viewModel.autoUpdateEnabled.collectAsStateWithLifecycle()
    var setupStep by remember { mutableStateOf(SetupStep.OverlayPermission) }
    var isOverlayPermissionGranted by remember { mutableStateOf(Settings.canDrawOverlays(context)) }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isOverlayPermissionGranted = Settings.canDrawOverlays(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
    ) {
        Icon(
            painter = painterResource(
                id = if (setupStep == SetupStep.OverlayPermission) R.drawable.layers_filled else R.drawable.update_rounded,
            ),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.Center)
                .size(320.dp)
                .offset(y = (-40).dp)
                .alpha(0.05f),
        )

        Crossfade(
            targetState = setupStep,
            animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
            label = "Setup Step",
            modifier = Modifier.fillMaxSize(),
        ) { step ->
            when (step) {
                SetupStep.OverlayPermission -> OverlayPermissionStep(
                    isOverlayPermissionGranted = isOverlayPermissionGranted,
                    onGrantPermission = {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            "package:${context.packageName}".toUri(),
                        )
                        context.startActivity(intent)
                    },
                    onNext = {
                        isOverlayPermissionGranted = Settings.canDrawOverlays(context)
                        if (isOverlayPermissionGranted) {
                            setupStep = SetupStep.Updates
                        }
                    },
                )

                SetupStep.Updates -> UpdatesStep(
                    autoUpdateEnabled = autoUpdateEnabled,
                    onAutoUpdateChanged = viewModel::setAutoUpdateEnabled,
                    onComplete = {
                        viewModel.completeSetup()
                        onSetupCompleted()
                    },
                )
            }
        }
    }
}

@Composable
private fun OverlayPermissionStep(isOverlayPermissionGranted: Boolean, onGrantPermission: () -> Unit, onNext: () -> Unit) {
    SetupStepContainer(
        icon = R.drawable.layers_filled,
        iconContentDescription = "Overlay Permission Icon",
        title = "Appear on Top",
        description = "To show FPS, RAM usage, battery temperature, and CPU temperature while you use other apps, " +
            "RvSystem Monitor needs permission to display over them.",
        footer = if (isOverlayPermissionGranted) {
            "Permission granted. Continue to the next step."
        } else {
            "You can always change this in Settings"
        },
        action = {
            Button(
                onClick = rememberHapticOnClick(onGrantPermission),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = if (isOverlayPermissionGranted) "Permission Granted" else "Grant Permission",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SetupNextButton(
                enabled = isOverlayPermissionGranted,
                onClick = onNext,
                contentDescription = "Continue to update settings",
            )
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UpdatesStep(autoUpdateEnabled: Boolean, onAutoUpdateChanged: (Boolean) -> Unit, onComplete: () -> Unit) {
    SetupStepContainer(
        icon = R.drawable.update_rounded,
        iconContentDescription = "Update Icon",
        title = "Check for Updates",
        description = "Choose whether RvSystem Monitor should automatically check for app updates on startup.",
        footer = "You can change this later in App settings",
        action = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .hapticClickable { onAutoUpdateChanged(!autoUpdateEnabled) }
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.update_rounded),
                                contentDescription = "Update Icon",
                                tint = MaterialTheme.colorScheme.onSecondary,
                            )
                        }

                        Column {
                            Text(
                                text = "Check for Updates",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "Automatically check for updates on startup",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Switch(
                        checked = autoUpdateEnabled,
                        onCheckedChange = onAutoUpdateChanged,
                        colors = SwitchDefaults.colors(
                            checkedIconColor = MaterialTheme.colorScheme.primary,
                        ),
                        thumbContent = {
                            Crossfade(
                                targetState = autoUpdateEnabled,
                                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                                label = "Setup Auto Update Switch Icon",
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            SetupNextButton(
                enabled = true,
                onClick = onComplete,
                contentDescription = "Complete setup",
            )
        },
    )
}

@Composable
private fun SetupStepContainer(
    icon: Int,
    iconContentDescription: String,
    title: String,
    description: String,
    footer: String,
    action: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = iconContentDescription,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            Spacer(modifier = Modifier.height(48.dp))

            action()
        }

        Text(
            text = footer,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun SetupNextButton(enabled: Boolean, onClick: () -> Unit, contentDescription: String) {
    FilledIconButton(
        onClick = rememberHapticOnClick(onClick),
        enabled = enabled,
        modifier = Modifier.size(56.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Icon(
            painter = painterResource(R.drawable.arrow_forward_ios_new),
            contentDescription = contentDescription,
        )
    }
}
