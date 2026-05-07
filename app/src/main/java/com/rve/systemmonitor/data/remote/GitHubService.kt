package com.rve.systemmonitor.data.remote

import com.rve.systemmonitor.domain.model.GitHubContributor
import com.rve.systemmonitor.domain.model.GitHubUser
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {
    @GET("repos/{owner}/{repo}/contributors")
    suspend fun getContributors(@Path("owner") owner: String, @Path("repo") repo: String): List<GitHubContributor>

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GitHubUser
}
