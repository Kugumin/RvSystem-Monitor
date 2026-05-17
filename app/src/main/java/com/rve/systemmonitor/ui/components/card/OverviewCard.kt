package com.rve.systemmonitor.ui.components.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * A generic overview card that provides a background icon slot and a content slot.
 *
 * @param modifier The [Modifier] to be applied to the card.
 * @param containerColor The background color of the card.
 * @param contentColor The color of the content inside the card.
 * @param iconResId Optional drawable resource ID to be displayed as a large background icon.
 * @param backgroundIcon A slot for a background icon or any other background decoration.
 * @param content The main content to be displayed in the card.
 */
@Composable
fun OverviewCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    iconResId: Int? = null,
    backgroundIcon: @Composable BoxScope.() -> Unit = {
        if (iconResId != null) {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(160.dp)
                    .offset(y = 30.dp)
                    .alpha(0.20f),
            )
        }
    },
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            backgroundIcon()
            Box(modifier = Modifier.padding(20.dp)) {
                content()
            }
        }
    }
}
