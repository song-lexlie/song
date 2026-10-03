package com.song.bookshelf.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.song.bookshelf.BuildConfig
import com.song.bookshelf.core.data.model.SpineDefault
import com.song.bookshelf.core.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ApiKeyStatus(val name: String, val configured: Boolean)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val spineDefault: StateFlow<SpineDefault?> = settingsRepository.spineDefault
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val apiKeys: List<ApiKeyStatus> = listOf(
        ApiKeyStatus("알라딘", BuildConfig.ALADIN_TTB_KEY.isNotBlank()),
        ApiKeyStatus("카카오", BuildConfig.KAKAO_REST_KEY.isNotBlank()),
        ApiKeyStatus("예스24 (선택)", BuildConfig.YES24_API_KEY.isNotBlank()),
    )

    fun setSpineDefault(value: SpineDefault) {
        viewModelScope.launch { settingsRepository.setSpineDefault(value) }
    }
}
