package com.example.tilefall.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.tilefall.model.Difficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tilefall_preferences")

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class UserSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val bestScore: Int = 0,
    val highestStreak: Int = 0,
    val lastDifficulty: Difficulty = Difficulty.MEDIUM
)

class GamePreferences(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val BEST_SCORE = intPreferencesKey("best_score")
        val HIGHEST_STREAK = intPreferencesKey("highest_streak")
        val LAST_DIFFICULTY = stringPreferencesKey("last_difficulty")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: AppThemeMode.SYSTEM.name
        val themeMode = try {
            AppThemeMode.valueOf(themeModeStr)
        } catch (_: Exception) {
            AppThemeMode.SYSTEM
        }

        val sound = preferences[PreferencesKeys.SOUND_ENABLED] ?: true
        val haptics = preferences[PreferencesKeys.HAPTICS_ENABLED] ?: true
        val bestScore = preferences[PreferencesKeys.BEST_SCORE] ?: 0
        val highestStreak = preferences[PreferencesKeys.HIGHEST_STREAK] ?: 0

        val diffStr = preferences[PreferencesKeys.LAST_DIFFICULTY] ?: Difficulty.MEDIUM.name
        val lastDifficulty = try {
            Difficulty.valueOf(diffStr)
        } catch (_: Exception) {
            Difficulty.MEDIUM
        }

        UserSettings(
            themeMode = themeMode,
            soundEnabled = sound,
            hapticsEnabled = haptics,
            bestScore = bestScore,
            highestStreak = highestStreak,
            lastDifficulty = lastDifficulty
        )
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTICS_ENABLED] = enabled
        }
    }

    suspend fun setLastDifficulty(difficulty: Difficulty) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_DIFFICULTY] = difficulty.name
        }
    }

    suspend fun updateHighScoreAndStreak(score: Int, streak: Int) {
        context.dataStore.edit { preferences ->
            val currentBestScore = preferences[PreferencesKeys.BEST_SCORE] ?: 0
            if (score > currentBestScore) {
                preferences[PreferencesKeys.BEST_SCORE] = score
            }
            val currentBestStreak = preferences[PreferencesKeys.HIGHEST_STREAK] ?: 0
            if (streak > currentBestStreak) {
                preferences[PreferencesKeys.HIGHEST_STREAK] = streak
            }
        }
    }

    suspend fun resetScores() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BEST_SCORE] = 0
            preferences[PreferencesKeys.HIGHEST_STREAK] = 0
        }
    }
}
