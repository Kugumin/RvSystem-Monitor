package com.rve.systemmonitor.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubContributor(
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("contributions") val contributions: Int,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("name") val name: String? = null,
)
