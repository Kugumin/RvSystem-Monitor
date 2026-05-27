package com.rve.systemmonitor.utils

import kotlinx.serialization.Serializable

@Serializable
enum class VibrationIntensity {
    LIGHT,
    MEDIUM,
    STRONG,
}
