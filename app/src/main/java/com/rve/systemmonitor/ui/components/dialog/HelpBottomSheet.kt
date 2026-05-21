package com.rve.systemmonitor.ui.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rve.systemmonitor.R
import com.rve.systemmonitor.ui.components.item.HelpItem
import kotlinx.collections.immutable.ImmutableList

/**
 * A shared component for the content of a help bottom sheet.
 *
 * @param title The title of the help section.
 * @param helpItems A list of pairs where each pair contains a title and a description.
 * @param modifier The [Modifier] to be applied to the container.
 */
@Composable
fun HelpBottomSheetContent(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.help_default_title),
    helpItems: ImmutableList<Pair<String, String>>,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(helpItems) { (title, description) ->
                HelpItem(
                    title = title,
                    description = description,
                )
            }
        }
    }
}
