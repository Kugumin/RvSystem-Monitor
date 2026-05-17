package com.rve.systemmonitor.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch

private enum class SetupStep {
    OverlayPermission,
    Updates,
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SetupScreen(viewModel: SetupViewModel = hiltViewModel(), onSetupCompleted: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val autoUpdateEnabled by viewModel.autoUpdateEnabled.collectAsStateWithLifecycle()
    var setupStep by remember { mutableStateOf(SetupStep.OverlayPermission) }
    var isOverlayPermissionGranted by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    var animationVisible by remember { mutableStateOf(false) }

    val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val spatialSpecInt = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()
    val slowSpatialSpec = MaterialTheme.motionScheme.slowSpatialSpec<IntOffset>()
    val slowEffectsSpec = MaterialTheme.motionScheme.slowEffectsSpec<Float>()

    LaunchedEffect(Unit) {
        delay(400)
        animationVisible = true
    }

    BackHandler(enabled = setupStep != SetupStep.OverlayPermission) {
        if (setupStep == SetupStep.Updates) {
            setupStep = SetupStep.OverlayPermission
        }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
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
                    modifier = Modifier.size(280.dp),
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                StepIndicator(
                    currentStep = setupStep.ordinal,
                    totalSteps = SetupStep.entries.size,
                )

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedContent(
                    targetState = setupStep,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            (slideInHorizontally(animationSpec = slowSpatialSpec) { it } + fadeIn(animationSpec = slowEffectsSpec))
                                .togetherWith(
                                    slideOutHorizontally(animationSpec = slowSpatialSpec) {
                                        -it
                                    } + fadeOut(animationSpec = slowEffectsSpec),
                                )
                        } else {
                            (slideInHorizontally(animationSpec = slowSpatialSpec) { -it } + fadeIn(animationSpec = slowEffectsSpec))
                                .togetherWith(
                                    slideOutHorizontally(animationSpec = slowSpatialSpec) {
                                        it
                                    } + fadeOut(animationSpec = slowEffectsSpec),
                                )
                        }
                    },
                    label = "Setup Step Content",
                    modifier = Modifier.fillMaxWidth(),
                ) { step ->
                    when (step) {
                        SetupStep.OverlayPermission -> OverlayPermissionContent(
                            isOverlayPermissionGranted = isOverlayPermissionGranted,
                        )

                        SetupStep.Updates -> UpdatesContent(
                            autoUpdateEnabled = autoUpdateEnabled,
                            onAutoUpdateChanged = viewModel::setAutoUpdateEnabled,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                val buttonText = when (setupStep) {
                    SetupStep.OverlayPermission -> if (isOverlayPermissionGranted) "Continue" else "Grant Permission"
                    SetupStep.Updates -> "Finish Setup"
                }

                val onButtonClick = when (setupStep) {
                    SetupStep.OverlayPermission -> {
                        {
                            if (isOverlayPermissionGranted) {
                                setupStep = SetupStep.Updates
                            } else {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    "package:${context.packageName}".toUri(),
                                )
                                context.startActivity(intent)
                            }
                        }
                    }

                    SetupStep.Updates -> {
                        {
                            scope.launch {
                                animationVisible = false
                                delay(400)
                                viewModel.completeSetup()
                                onSetupCompleted()
                            }
                            Unit
                        }
                    }
                }

                Button(
                    onClick = rememberHapticOnClick(onButtonClick),
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
                        text = buttonText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val footerText = when (setupStep) {
                    SetupStep.OverlayPermission -> if (isOverlayPermissionGranted) {
                        "Permission granted. Continue to the next step."
                    } else {
                        "Required to show the monitor overlay"
                    }

                    SetupStep.Updates -> "You can change this later in Settings"
                }

                Text(
                    text = footerText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalSteps) { index ->
            val isSelected = index == currentStep
            val width by animateDpAsState(
                targetValue = if (isSelected) 32.dp else 8.dp,
                animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
                label = "Step Indicator Width",
            )
            val color by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                },
                animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
                label = "Step Indicator Color",
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

@Composable
private fun OverlayPermissionContent(isOverlayPermissionGranted: Boolean) {
    SetupStepContent(
        title = "Appear on Top",
        description = "To show FPS, RAM usage, and temperatures while you use other apps, " +
            "RvSystem Monitor needs permission to display over them.",
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UpdatesContent(autoUpdateEnabled: Boolean, onAutoUpdateChanged: (Boolean) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    SetupStepContent(
        title = "Check for Updates",
        description = "Automatically check for app updates on startup to stay up to date with the latest features.",
        action = {
            Card(
                onClick = rememberHapticOnClick { onAutoUpdateChanged(!autoUpdateEnabled) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                interactionSource = interactionSource,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
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
                                .background(MaterialTheme.colorScheme.tertiaryContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.update_rounded),
                                contentDescription = "Update Icon",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                        }

                        Column {
                            Text(
                                text = "Auto Update",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "Check on startup",
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
private fun SetupStepContent(title: String, description: String, action: (@Composable ColumnScope.() -> Unit)? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        if (action != null) {
            Spacer(modifier = Modifier.height(32.dp))
            action()
        }
    }
}
