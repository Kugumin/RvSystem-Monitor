package com.rve.systemmonitor.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRelease(
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("name")
    val name: String,
    @SerialName("body")
    val body: String,
    @SerialName("assets")
    val assets: List<GitHubAsset>,
)
