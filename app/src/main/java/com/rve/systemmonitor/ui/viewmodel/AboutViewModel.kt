package com.rve.systemmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rve.systemmonitor.domain.model.GitHubContributor
import com.rve.systemmonitor.domain.repository.AboutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AboutViewModel @Inject constructor(private val aboutRepository: AboutRepository) : ViewModel() {

    private val _contributors = MutableStateFlow<List<GitHubContributor>>(emptyList())
    val contributors: StateFlow<List<GitHubContributor>> = _contributors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchContributors()
    }

    fun fetchContributors() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            aboutRepository.getContributors()
                .onSuccess {
                    _contributors.value = it
                }
                .onFailure {
                    _error.value = "No internet connection or failed to fetch contributors"
                }
            _isLoading.value = false
        }
    }
}
