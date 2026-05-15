package com.rve.systemmonitor.ui.components.row

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rve.systemmonitor.ui.components.chip.BadgeChip

/**
 * A row component that displays a progress indicator along with detailed memory or storage information.
 *
 * @param label The label for the data (e.g., "RAM", "Internal Storage").
 * @param usedValue The current used value in GB.
 * @param totalValue The total capacity value in GB.
 * @param usedPercentage The percentage of capacity currently in use.
 * @param freeValue The available free space in GB.
 * @param progressColor The color of the progress indicator.
 * @param modifier The [Modifier] to be applied to the row.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MemoryStorageProgressRow(
    modifier: Modifier = Modifier,
    label: String,
    usedValue: String,
    totalValue: String,
    usedPercentage: Float,
    freeValue: String,
    progressColor: Color = MaterialTheme.colorScheme.primary,
) {
    val density = LocalDensity.current
    val customStroke = remember(density) {
        with(density) {
            Stroke(
                width = 12.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }

    val progress by animateFloatAsState(
        targetValue = if (usedPercentage.isNaN()) 0f else (usedPercentage / 100f),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "$label Progress Animation",
    )

    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CircularWavyProgressIndicator(
            progress = { progress },
            color = progressColor,
            trackColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.4f),
            stroke = customStroke,
            trackStroke = customStroke,
            wavelength = 25.dp,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f),
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Text(
                    text = "$usedValue / $totalValue GB",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BadgeChip(
                    text = "${usedPercentage.toInt()}%",
                    containerColor = progressColor,
                    textColor = if (progressColor == MaterialTheme.colorScheme.primary) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onTertiary
                    },
                )
                BadgeChip(
                    text = "$freeValue GB Free",
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    textColor = MaterialTheme.colorScheme.onTertiary,
                )
            }
        }
    }
}
