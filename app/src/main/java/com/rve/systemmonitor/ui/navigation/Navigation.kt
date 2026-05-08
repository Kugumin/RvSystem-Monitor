package com.rve.systemmonitor.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rve.systemmonitor.RvSystemMonitorApp
import com.rve.systemmonitor.ui.components.ScreenWrapper
import com.rve.systemmonitor.ui.screens.AboutScreen
import com.rve.systemmonitor.ui.screens.AppSettingsScreen
import com.rve.systemmonitor.ui.screens.AppearanceSettingsScreen
import com.rve.systemmonitor.ui.screens.MonitoringSettingsScreen
import com.rve.systemmonitor.ui.screens.OverlaySettingsScreen
import com.rve.systemmonitor.ui.screens.SettingsScreen
import com.rve.systemmonitor.ui.screens.SetupScreen

@Composable
fun AppNavigation(isSetupCompleted: Boolean) {
    val navController = rememberNavController()
    val startDestination = remember {
        if (isSetupCompleted) Route.Main else Route.Setup
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
    ) {
        composable<Route.Setup>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            SetupScreen(
                onPermissionGranted = {
                    navController.navigateSafely(Route.Main) {
                        popUpTo(Route.Setup) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigateSafely(Route.Main) {
                        popUpTo(Route.Setup) { inclusive = true }
                    }
                },
            )
        }

        composable<Route.Main>(
            enterTransition = {
                mainRootEnterTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route,
                    fallback = enterTransition(),
                )
            },
            exitTransition = {
                mainRootExitTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route,
                    fallback = exitTransition(),
                )
            },
            popEnterTransition = {
                mainRootEnterTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route,
                    fallback = popEnterTransition(),
                )
            },
            popExitTransition = {
                mainRootExitTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route,
                    fallback = popExitTransition(),
                )
            },
        ) {
            ScreenWrapper(navController = navController) {
                RvSystemMonitorApp(
                    onNavigateToSettings = { navController.navigateSafely(Route.Settings) },
                )
            }
        }

        composable<Route.Settings>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            ScreenWrapper(navController = navController) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToApp = { navController.navigateSafely(Route.AppSettings) },
                    onNavigateToAppearance = { navController.navigateSafely(Route.AppearanceSettings) },
                    onNavigateToMonitoring = { navController.navigateSafely(Route.MonitoringSettings) },
                    onNavigateToOverlay = { navController.navigateSafely(Route.OverlaySettings) },
                    onNavigateToAbout = { navController.navigateSafely(Route.About) },
                )
            }
        }

        composable<Route.AppSettings>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            ScreenWrapper(navController = navController) {
                AppSettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }
        }

        composable<Route.About>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            ScreenWrapper(navController = navController) {
                AboutScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }
        }

        composable<Route.AppearanceSettings>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            ScreenWrapper(navController = navController) {
                AppearanceSettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }
        }

        composable<Route.MonitoringSettings>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            ScreenWrapper(navController = navController) {
                MonitoringSettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }
        }

        composable<Route.OverlaySettings>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            ScreenWrapper(navController = navController) {
                OverlaySettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }
        }
    }
}
