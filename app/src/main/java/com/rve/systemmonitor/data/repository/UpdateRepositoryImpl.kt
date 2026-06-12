package com.rve.systemmonitor.data.repository

import android.app.Application
import com.rve.systemmonitor.data.remote.GitHubService
import com.rve.systemmonitor.domain.model.GitHubRelease
import com.rve.systemmonitor.domain.repository.DownloadStatus
import com.rve.systemmonitor.domain.repository.UpdateRepository
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UpdateRepositoryImpl @Inject constructor(private val gitHubService: GitHubService, private val application: Application) :
    UpdateRepository {

    override suspend fun getLatestRelease(): Result<GitHubRelease> {
        return try {
            val release = gitHubService.getLatestRelease("Rve27", "RvSystem-Monitor")
            val latestChangelog = getLatestFastlaneChangelog().getOrNull()
            Result.success(
                if (latestChangelog.isNullOrBlank()) release else release.copy(body = latestChangelog),
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getLatestFastlaneChangelog(): Result<String> {
        return try {
            val changelog = gitHubService.getRepositoryContents(
                owner = "Rve27",
                repo = "RvSystem-Monitor",
                path = "fastlane/metadata/android/en-US/changelogs",
            )
                .asSequence()
                .filter { it.name.endsWith(".txt") }
                .mapNotNull { content ->
                    val versionCode = content.name.removeSuffix(".txt").toIntOrNull() ?: return@mapNotNull null
                    content.downloadUrl?.let { versionCode to it }
                }
                .maxByOrNull { it.first }
                ?.second

            if (changelog == null) {
                Result.failure(IllegalStateException("No changelog file found"))
            } else {
                val response = gitHubService.downloadFile(changelog)
                val body = response.body()?.string()
                if (response.isSuccessful && !body.isNullOrBlank()) {
                    Result.success(body)
                } else {
                    Result.failure(IllegalStateException("Failed to load changelog: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun downloadApk(url: String): Flow<DownloadStatus> = flow {
        emit(DownloadStatus.Downloading(0f))
        val response = gitHubService.downloadFile(url)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val file = File(application.cacheDir, "app-github-release.apk")
                val totalBytes = body.contentLength()

                body.byteStream().use { input ->
                    file.outputStream().use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var totalBytesRead = 0L

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            if (totalBytes > 0) {
                                emit(DownloadStatus.Downloading((totalBytesRead.toFloat() / totalBytes).coerceIn(0f, 1f)))
                            }
                        }
                    }
                }
                emit(DownloadStatus.Downloading(1f))
                emit(DownloadStatus.Finished(file))
            } else {
                emit(DownloadStatus.Error("Empty response body"))
            }
        } else {
            emit(DownloadStatus.Error("Failed to download file: ${response.code()}"))
        }
    }.catch { e ->
        if (e is CancellationException) throw e
        emit(DownloadStatus.Error(e.message ?: "Unknown error"))
    }.flowOn(Dispatchers.IO)
}
