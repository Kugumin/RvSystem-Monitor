package com.rve.systemmonitor.data.repository

import android.app.Application
import com.rve.systemmonitor.data.remote.GitHubService
import com.rve.systemmonitor.domain.model.GitHubRelease
import com.rve.systemmonitor.domain.repository.DownloadStatus
import com.rve.systemmonitor.domain.repository.UpdateRepository
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UpdateRepositoryImpl @Inject constructor(private val gitHubService: GitHubService, private val application: Application) :
    UpdateRepository {

    override suspend fun getLatestRelease(): Result<GitHubRelease> {
        return try {
            val release = gitHubService.getLatestRelease("Rve27", "RvSystem-Monitor")
            Result.success(release)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun downloadApk(url: String): Flow<DownloadStatus> = flow {
        emit(DownloadStatus.Downloading(0f))
        try {
            val response = gitHubService.downloadFile(url)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val file = File(application.cacheDir, "update.apk")
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
                                    emit(DownloadStatus.Downloading(totalBytesRead.toFloat() / totalBytes))
                                }
                            }
                        }
                    }
                    emit(DownloadStatus.Finished(file))
                } else {
                    emit(DownloadStatus.Error("Empty response body"))
                }
            } else {
                emit(DownloadStatus.Error("Failed to download file: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(DownloadStatus.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)
}
