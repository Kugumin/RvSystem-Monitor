package com.rve.systemmonitor.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset

/**
 * Public constants for external use.
 */
const val TRANSITION_DURATION = NavMotion.StandardDuration

/**
 * Navigation motion constants for RvSystem-Monitor.
 * Follows Material 3 Expressive guidelines for emphasized motion.
 */
private object NavMotion {
    const val StandardDuration = 450
    const val FastDuration = StandardDuration / 2
    const val BottomNavDuration = 400

    // Custom cubic-bezier curves for expressive navigation
    val StandardCurve = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val EnteringCurve = CubicBezierEasing(0.2f, 0.85f, 0.7f, 1f)
    val ExitingCurve = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
}

/**
 * Creates a [tween] animation spec using the project's default navigation motion.
 */
private fun <T> navTween(duration: Int = NavMotion.StandardDuration, easing: Easing = NavMotion.EnteringCurve) =
    tween<T>(durationMillis = duration, easing = easing)

fun enterTransition(): EnterTransition {
    val spec = navTween<Float>()
    return slideInHorizontally(
        animationSpec = navTween(),
        initialOffsetX = { (it * 0.5f).toInt() },
    ) + scaleIn(
        animationSpec = navTween(),
        initialScale = 0.92f,
        transformOrigin = TransformOrigin.Center,
    ) + fadeIn(
        animationSpec = navTween(easing = NavMotion.ExitingCurve),
    )
}

fun exitTransition(): ExitTransition = slideOutHorizontally(
    animationSpec = navTween(easing = NavMotion.ExitingCurve),
    targetOffsetX = { -(it * 0.25f).toInt() },
) + fadeOut(
    animationSpec = navTween(duration = NavMotion.FastDuration, easing = NavMotion.ExitingCurve),
)

fun popEnterTransition(): EnterTransition = slideInHorizontally(
    animationSpec = navTween(),
    initialOffsetX = { -(it * 0.25f).toInt() },
) + scaleIn(
    animationSpec = navTween(),
    initialScale = 0.95f,
) + fadeIn(
    animationSpec = navTween(duration = NavMotion.FastDuration),
)

fun popExitTransition(): ExitTransition = slideOutHorizontally(
    animationSpec = navTween(easing = NavMotion.ExitingCurve),
    targetOffsetX = { (it * 0.5f).toInt() },
) + scaleOut(
    animationSpec = navTween(easing = NavMotion.ExitingCurve),
    targetScale = 0.92f,
    transformOrigin = TransformOrigin.Center,
) + fadeOut(
    animationSpec = navTween(duration = NavMotion.FastDuration, easing = NavMotion.ExitingCurve),
)

enum class MainRootDirection {
    FORWARD,
    BACKWARD,
}

private val MAIN_ROOT_TRANSITION_SPEC =
    tween<IntOffset>(durationMillis = NavMotion.BottomNavDuration, easing = NavMotion.StandardCurve)

private val MAIN_ROOT_FADE_SPEC =
    tween<Float>(durationMillis = NavMotion.BottomNavDuration, easing = NavMotion.StandardCurve)

fun mainRootDirection(fromRoute: String?, toRoute: String?): MainRootDirection? {
    val fromIndex = mainRootRouteIndex(fromRoute) ?: return null
    val toIndex = mainRootRouteIndex(toRoute) ?: return null
    if (fromIndex == toIndex) return null
    return if (toIndex > fromIndex) MainRootDirection.FORWARD else MainRootDirection.BACKWARD
}

private fun mainRootRouteIndex(route: String?): Int? = when {
    route?.contains("Main") == true -> 0
    else -> null
}

fun mainRootEnterTransition(fromRoute: String?, toRoute: String?, fallback: EnterTransition): EnterTransition =
    when (mainRootDirection(fromRoute, toRoute)) {
        MainRootDirection.FORWARD -> {
            slideInHorizontally(
                animationSpec = MAIN_ROOT_TRANSITION_SPEC,
                initialOffsetX = { it },
            ) + fadeIn(animationSpec = MAIN_ROOT_FADE_SPEC)
        }

        MainRootDirection.BACKWARD -> {
            slideInHorizontally(
                animationSpec = MAIN_ROOT_TRANSITION_SPEC,
                initialOffsetX = { -it },
            ) + fadeIn(animationSpec = MAIN_ROOT_FADE_SPEC)
        }

        null -> fallback
    }

fun mainRootExitTransition(fromRoute: String?, toRoute: String?, fallback: ExitTransition): ExitTransition =
    when (mainRootDirection(fromRoute, toRoute)) {
        MainRootDirection.FORWARD -> {
            slideOutHorizontally(
                animationSpec = MAIN_ROOT_TRANSITION_SPEC,
                targetOffsetX = { -it },
            ) + fadeOut(animationSpec = MAIN_ROOT_FADE_SPEC)
        }

        MainRootDirection.BACKWARD -> {
            slideOutHorizontally(
                animationSpec = MAIN_ROOT_TRANSITION_SPEC,
                targetOffsetX = { it },
            ) + fadeOut(animationSpec = MAIN_ROOT_FADE_SPEC)
        }

        null -> fallback
    }
