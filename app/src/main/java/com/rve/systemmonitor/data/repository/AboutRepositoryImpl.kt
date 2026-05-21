package com.rve.systemmonitor.data.repository

import com.rve.systemmonitor.data.remote.GitHubService
import com.rve.systemmonitor.domain.model.GitHubContributor
import com.rve.systemmonitor.domain.repository.AboutRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AboutRepositoryImpl @Inject constructor(private val gitHubService: GitHubService) : AboutRepository {

    private var cachedContributors: List<GitHubContributor>? = null
    private val mutex = Mutex()

    override suspend fun getContributors(): Result<List<GitHubContributor>> {
        return mutex.withLock {
            cachedContributors?.let {
                return@withLock Result.success(it)
            }

            try {
                val baseContributors = gitHubService.getContributors("Rve27", "RvSystem-Monitor")

                // Fetch detailed user info to get the "name" field for each contributor
                val detailedContributors = coroutineScope {
                    baseContributors.map { contributor ->
                        async {
                            try {
                                val user = gitHubService.getUser(contributor.login)
                                contributor.copy(name = user.name)
                            } catch (e: Exception) {
                                contributor // Fallback to basic info on error
                            }
                        }
                    }.awaitAll()
                }

                cachedContributors = detailedContributors
                Result.success(detailedContributors)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
