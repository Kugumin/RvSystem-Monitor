package com.rve.systemmonitor.domain.repository

import com.rve.systemmonitor.domain.model.GitHubContributor

interface AboutRepository {
    suspend fun getContributors(): Result<List<GitHubContributor>>
}
