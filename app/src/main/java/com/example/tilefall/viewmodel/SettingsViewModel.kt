package com.example.tilefall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tilefall.data.AppThemeMode
import com.example.tilefall.data.GamePreferences
import com.example.tilefall.data.UserSettings
import com.example.tilefall.model.Difficulty
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application)

    val settings: StateFlow<UserSettings> = preferences.userSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings()
    )

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            preferences.setThemeMode(mode)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setSoundEnabled(enabled)
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setHapticsEnabled(enabled)
        }
    }

    fun setLastDifficulty(difficulty: Difficulty) {
        viewModelScope.launch {
            preferences.setLastDifficulty(difficulty)
        }
    }

    fun resetBestScore() {
        viewModelScope.launch {
            preferences.resetScores()
        }
    }
}
