package com.rve.systemmonitor.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

/**
 * A generic composable function to encapsulate the logic of conditionally collecting a [StateFlow]
 * based on whether the screen is active.
 *
 * @param isActive Whether the screen is currently active and should receive updates.
 * @param stateFlow The StateFlow to collect from when active.
 * @param initialValue The initial value to use when the flow is not being collected.
 */
@Composable
fun <T> rememberLifecycleAwareState(isActive: Boolean, stateFlow: StateFlow<T>, initialValue: T = stateFlow.value): State<T> {
    return if (isActive) {
        stateFlow.collectAsStateWithLifecycle()
    } else {
        remember { emptyFlow<T>() }.collectAsStateWithLifecycle(initialValue)
    }
}
