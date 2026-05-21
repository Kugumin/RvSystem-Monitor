package com.rve.systemmonitor.ui.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rve.systemmonitor.R
import com.rve.systemmonitor.ui.components.haptic.rememberHapticOnClick

/**
 * A standard information dialog with a title, description, and a close button.
 *
 * @param title The title text of the dialog.
 * @param description The body text of the dialog.
 * @param onDismiss Callback invoked when the dialog is dismissed or the close button is clicked.
 */
@Composable
fun InfoDialog(title: String, description: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            TextButton(onClick = rememberHapticOnClick(onDismiss)) {
                Text(stringResource(R.string.button_close))
            }
        },
    )
}
