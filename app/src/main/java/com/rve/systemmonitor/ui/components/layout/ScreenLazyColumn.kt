package com.rve.systemmonitor.ui.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A wrapper around [LazyColumn] that applies the standard configuration used across all root lists.
 */
@Composable
fun ScreenLazyColumn(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 16.dp, bottom = 112.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        content = content,
    )
}
