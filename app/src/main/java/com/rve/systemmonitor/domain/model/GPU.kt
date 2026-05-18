package com.rve.systemmonitor.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class GPU(
    val renderer: String = "unknown",
    val vendor: String = "unknown",
    val glesVersion: String = "unknown",
    val detailedGlesVersion: String = "unknown",
    val vulkanVersion: String = "unknown",
    val vulkanDriverVersion: String = "unknown",
    val temperature: Double = 0.0,
)
