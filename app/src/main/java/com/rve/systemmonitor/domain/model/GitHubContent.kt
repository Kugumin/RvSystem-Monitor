package com.rve.systemmonitor.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubContent(
    @SerialName("name")
    val name: String,
    @SerialName("download_url")
    val downloadUrl: String? = null,
)
