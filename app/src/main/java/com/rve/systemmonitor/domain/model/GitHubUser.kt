package com.rve.systemmonitor.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubUser(@SerialName("login") val login: String, @SerialName("name") val name: String?)
