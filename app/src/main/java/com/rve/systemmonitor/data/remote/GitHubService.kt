package com.rve.systemmonitor.data.remote

import com.rve.systemmonitor.domain.model.GitHubContributor
import com.rve.systemmonitor.domain.model.GitHubRelease
import com.rve.systemmonitor.domain.model.GitHubUser
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url

interface GitHubService {
    @GET("repos/{owner}/{repo}/contributors")
    suspend fun getContributors(@Path("owner") owner: String, @Path("repo") repo: String): List<GitHubContributor>

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GitHubUser

    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestRelease(@Path("owner") owner: String, @Path("repo") repo: String): GitHubRelease

    @Streaming
    @GET
    suspend fun downloadFile(@Url url: String): Response<ResponseBody>
}
