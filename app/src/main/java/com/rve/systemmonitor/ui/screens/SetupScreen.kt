package com.rve.systemmonitor.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import com.rve.systemmonitor.R
import com.rve.systemmonitor.ui.components.haptic.rememberHapticOnClick
import com.rve.systemmonitor.ui.viewmodel.SetupViewModel
import kotlinx.coroutines.delay

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
    var animationVisible by remember { mutableStateOf(false) }

    val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val spatialSpecInt = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()

    LaunchedEffect(Unit) {
        delay(400)
        animationVisible = true
    }

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
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center,
            ) {
                this@Column.AnimatedVisibility(
                    visible = animationVisible,
                    enter = fadeIn(animationSpec = effectsSpec) +
                        slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = spatialSpecInt,
                        ) +
                        scaleIn(
                            initialScale = 0.8f,
                            animationSpec = spatialSpec,
                        ),
                    exit = fadeOut(animationSpec = effectsSpec) +
                        slideOutVertically(
                            targetOffsetY = { it / 2 },
                            animationSpec = spatialSpecInt,
                        ) +
                        scaleOut(
                            targetScale = 0.8f,
                            animationSpec = spatialSpec,
                        ),
                ) {
                    DotLottieAnimation(
                        source = DotLottieSource.Res(R.raw.cat),
                        autoplay = true,
                        loop = true,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Crossfade(
                targetState = setupStep,
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                label = "Setup Step Content",
            ) { step ->
                when (step) {
                    SetupStep.OverlayPermission -> OverlayPermissionContent(
                        isOverlayPermissionGranted = isOverlayPermissionGranted,
                        onGrantPermission = {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                "package:${context.packageName}".toUri(),
                            )
                            context.startActivity(intent)
                        },
                    )

                    SetupStep.Updates -> UpdatesContent(
                        autoUpdateEnabled = autoUpdateEnabled,
                        onAutoUpdateChanged = viewModel::setAutoUpdateEnabled,
                    )
                }
            }
        }

        Crossfade(
            targetState = setupStep,
            animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
            label = "Setup Footer",
            modifier = Modifier.fillMaxSize(),
        ) { step ->
            Box(modifier = Modifier.fillMaxSize()) {
                val footerText = when (step) {
                    SetupStep.OverlayPermission -> if (isOverlayPermissionGranted) {
                        "Permission granted. Continue to the next step."
                    } else {
                        "You can always change this in Settings"
                    }

                    SetupStep.Updates -> "You can change this later in App settings"
                }

                val nextButtonEnabled = when (step) {
                    SetupStep.OverlayPermission -> isOverlayPermissionGranted
                    SetupStep.Updates -> true
                }

                val onNextClick = when (step) {
                    SetupStep.OverlayPermission -> {
                        {
                            isOverlayPermissionGranted = Settings.canDrawOverlays(context)
                            if (isOverlayPermissionGranted) {
                                setupStep = SetupStep.Updates
                            }
                        }
                    }

                    SetupStep.Updates -> {
                        {
                            viewModel.completeSetup()
                            onSetupCompleted()
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp),
                ) {
                    SetupNextButton(
                        enabled = nextButtonEnabled,
                        onClick = onNextClick,
                        contentDescription = if (step == SetupStep.OverlayPermission) "Continue" else "Complete",
                    )
                }

                Text(
                    text = footerText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun OverlayPermissionContent(isOverlayPermissionGranted: Boolean, onGrantPermission: () -> Unit) {
    SetupStepContent(
        title = "Appear on Top",
        description = "To show FPS, RAM usage, battery temperature, and CPU temperature while you use other apps, " +
            "RvSystem Monitor needs permission to display over them.",
        action = {
            Button(
                onClick = rememberHapticOnClick(onGrantPermission),
                enabled = !isOverlayPermissionGranted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shapes = ButtonDefaults.shapes(),
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
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UpdatesContent(autoUpdateEnabled: Boolean, onAutoUpdateChanged: (Boolean) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    SetupStepContent(
        title = "Check for Updates",
        description = "Choose whether RvSystem Monitor should automatically check for app updates on startup.",
        action = {
            Card(
                onClick = rememberHapticOnClick { onAutoUpdateChanged(!autoUpdateEnabled) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                interactionSource = interactionSource,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        interactionSource = interactionSource,
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
        },
    )
}

@Composable
private fun SetupStepContent(title: String, description: String, action: @Composable ColumnScope.() -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
}

@Composable
private fun SetupNextButton(enabled: Boolean, onClick: () -> Unit, contentDescription: String) {
    FilledIconButton(
        onClick = rememberHapticOnClick(onClick),
        enabled = enabled,
        modifier = Modifier.size(56.dp),
        shapes = IconButtonDefaults.shapes(),
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
