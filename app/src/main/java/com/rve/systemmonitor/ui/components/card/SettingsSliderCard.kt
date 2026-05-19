package com.rve.systemmonitor.ui.components.card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rve.systemmonitor.R
import com.rve.systemmonitor.ui.components.haptic.rememberHapticOnClick

/**
 * A reusable card for settings that involve a slider.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsSliderCard(
    title: String,
    description: String,
    iconRes: Int,
    sliderState: SliderState,
    currentDisplayValue: Float,
    displayValueFormatter: (Float) -> String,
    onReset: () -> Unit,
    isResetVisible: Boolean,
    modifier: Modifier = Modifier,
    valueLabel: String = "Refresh Rate",
    enabled: Boolean = true,
    alpha: Float = 1f,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (enabled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = if (enabled) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f),
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = valueLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = displayValueFormatter(currentDisplayValue),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (enabled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                        )
                        AnimatedVisibility(
                            visible = enabled && isResetVisible,
                            enter = slideInHorizontally(
                                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                            ) { it } + expandHorizontally(
                                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                            ) + fadeIn(
                                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                            ),
                            exit = slideOutHorizontally(
                                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                            ) { it } + shrinkHorizontally(
                                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                            ) + fadeOut(
                                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                            ),
                        ) {
                            Row {
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = rememberHapticOnClick(onReset),
                                    modifier = Modifier.size(24.dp),
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.reset_settings_rounded),
                                        contentDescription = "Reset to default",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
                    }
                }

                Slider(
                    state = sliderState,
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth(),
                    track = {
                        SliderDefaults.Track(
                            sliderState = sliderState,
                            modifier = Modifier.height(36.dp),
                            trackCornerSize = 12.dp,
                        )
                    },
                )
            }
        }
    }
}
