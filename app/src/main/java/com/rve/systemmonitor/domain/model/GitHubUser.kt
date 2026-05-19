package com.rve.systemmonitor.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class GitHubUser(@SerialName("login") val login: String, @SerialName("name") val name: String?)
