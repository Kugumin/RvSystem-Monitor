package com.rve.systemmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rve.systemmonitor.BuildConfig
import com.rve.systemmonitor.domain.model.GitHubRelease
import com.rve.systemmonitor.domain.repository.DownloadStatus
import com.rve.systemmonitor.domain.repository.UpdateRepository
import com.rve.systemmonitor.utils.VersionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class UpdateViewModel @Inject constructor(private val updateRepository: UpdateRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val uiState: StateFlow<UpdateUiState> = _uiState.asStateFlow()
    private var downloadJob: Job? = null

    fun checkForUpdates() {
        if (_uiState.value is UpdateUiState.Checking ||
            _uiState.value is UpdateUiState.UpdateAvailable ||
            _uiState.value is UpdateUiState.Downloading ||
            _uiState.value is UpdateUiState.ReadyToInstall
        ) return

        viewModelScope.launch {
            _uiState.value = UpdateUiState.Checking
            updateRepository.getLatestRelease()
                .onSuccess { release ->
                    if (VersionUtils.compareVersions(release.tagName, BuildConfig.VERSION_NAME) > 0) {
                        _uiState.value = UpdateUiState.UpdateAvailable(release)
                    } else {
                        _uiState.value = UpdateUiState.Idle
                    }
                }
                .onFailure {
                    _uiState.value = UpdateUiState.Idle
                }
        }
    }

    fun downloadAndInstall(release: GitHubRelease) {
        val targetApkName = if (BuildConfig.DEBUG) "app-debug.apk" else "app-release.apk"
        val apkAsset = release.assets.find { it.name == targetApkName }
            ?: release.assets.find { it.name.endsWith(".apk") }

        if (apkAsset == null) {
            _uiState.value = UpdateUiState.Error("No APK found in the latest release")
            return
        }

        downloadJob?.cancel()
        downloadJob = viewModelScope.launch {
            updateRepository.downloadApk(apkAsset.browserDownloadUrl).collect { status ->
                when (status) {
                    is DownloadStatus.Downloading -> {
                        _uiState.value = UpdateUiState.Downloading(status.progress)
                    }

                    is DownloadStatus.Finished -> {
                        _uiState.value = UpdateUiState.ReadyToInstall(status.file)
                    }

                    is DownloadStatus.Error -> {
                        _uiState.value = UpdateUiState.Error(status.message)
                    }

                    else -> {}
                }
            }
        }
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        downloadJob = null
        _uiState.value = UpdateUiState.Idle
    }

    fun resetState() {
        _uiState.value = UpdateUiState.Idle
    }
}

sealed class UpdateUiState {
    data object Idle : UpdateUiState()
    data object Checking : UpdateUiState()
    data class UpdateAvailable(val release: GitHubRelease) : UpdateUiState()
    data class Downloading(val progress: Float) : UpdateUiState()
    data class ReadyToInstall(val file: File) : UpdateUiState()
    data class Error(val message: String) : UpdateUiState()
}
