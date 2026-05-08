package com.rve.systemmonitor.domain.repository

import com.rve.systemmonitor.domain.model.GitHubRelease
import java.io.File
import kotlinx.coroutines.flow.Flow

interface UpdateRepository {
    suspend fun getLatestRelease(): Result<GitHubRelease>
    fun downloadApk(url: String): Flow<DownloadStatus>
}

sealed class DownloadStatus {
    data object Idle : DownloadStatus()
    data class Downloading(val progress: Float) : DownloadStatus()
    data class Finished(val file: File) : DownloadStatus()
    data class Error(val message: String) : DownloadStatus()
}
