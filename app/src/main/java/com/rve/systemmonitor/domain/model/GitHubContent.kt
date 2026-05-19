package com.rve.systemmonitor.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class GitHubContent(
    @SerialName("name")
    val name: String,
    @SerialName("download_url")
    val downloadUrl: String? = null,
)
