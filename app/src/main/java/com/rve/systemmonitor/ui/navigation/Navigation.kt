package com.rve.systemmonitor.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rve.systemmonitor.RvSystemMonitorApp
import com.rve.systemmonitor.ui.components.ScreenWrapper
import com.rve.systemmonitor.ui.screens.AboutScreen
import com.rve.systemmonitor.ui.screens.AppSettingsScreen
import com.rve.systemmonitor.ui.screens.AppearanceSettingsScreen
import com.rve.systemmonitor.ui.screens.GPUScreen
import com.rve.systemmonitor.ui.screens.MonitoringSettingsScreen
import com.rve.systemmonitor.ui.screens.OverlaySettingsScreen
import com.rve.systemmonitor.ui.screens.RustLibraryScreen
import com.rve.systemmonitor.ui.screens.SettingsScreen
import com.rve.systemmonitor.ui.screens.SetupScreen

@Composable
fun AppNavigation(isSetupCompleted: Boolean) {
    val navController = rememberNavController()
    val startDestination = remember {
        if (isSetupCompleted) Route.Main else Route.Setup(isTestFlow = false)
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
        ) { backStackEntry ->
            val setup: Route.Setup = backStackEntry.toRoute()
            SetupScreen(
                onSetupCompleted = {
                    if (setup.isTestFlow) {
                        navController.popBackStack()
                    } else {
                        navController.navigateSafely(Route.Main) {
                            popUpTo(Route.Setup(isTestFlow = false)) { inclusive = true }
                        }
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
                    onNavigateToGPU = { navController.navigateSafely(Route.GPU) },
                )
            }
        }

        composable<Route.GPU>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            GPUScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() },
            )
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
                    onNavigateToRustLibrary = { navController.navigateSafely(Route.RustLibrary) },
                    onNavigateToAbout = { navController.navigateSafely(Route.About) },
                )
            }
        }

        composable<Route.RustLibrary>(
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() },
        ) {
            ScreenWrapper(navController = navController) {
                RustLibraryScreen(
                    onNavigateBack = { navController.popBackStack() },
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
                    onNavigateToSetup = { navController.navigateSafely(Route.Setup(isTestFlow = true)) },
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
