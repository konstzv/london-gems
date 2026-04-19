package com.londongemsapp.data.local

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import com.londongemsapp.presentation.theme.ThemeMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncPreferences @Inject constructor(
    private val prefs: SharedPreferences
) {
    private val lastSyncFlow = MutableStateFlow(
        prefs.getLong(KEY_LAST_SYNC, 0L).takeIf { it > 0 }
    )

    fun getLastSyncTimestamp(): Flow<Long?> = lastSyncFlow

    fun setLastSyncTimestamp(timestamp: Long) {
        prefs.edit().putLong(KEY_LAST_SYNC, timestamp).apply()
        lastSyncFlow.value = timestamp
    }

    private val syncIntervalFlow = MutableStateFlow(
        prefs.getLong(KEY_SYNC_INTERVAL, DEFAULT_SYNC_INTERVAL_MINUTES)
    )

    fun getSyncIntervalMinutes(): Flow<Long> = syncIntervalFlow

    fun getSyncIntervalMinutesValue(): Long = syncIntervalFlow.value

    fun setSyncIntervalMinutes(minutes: Long) {
        prefs.edit().putLong(KEY_SYNC_INTERVAL, minutes).apply()
        syncIntervalFlow.value = minutes
    }

    private val themeModeFlow = MutableStateFlow(
        ThemeMode.entries.getOrElse(
            prefs.getInt(KEY_THEME_MODE, ThemeMode.SYSTEM.ordinal)
        ) { ThemeMode.SYSTEM }
    )

    fun getThemeMode(): Flow<ThemeMode> = themeModeFlow

    fun getThemeModeValue(): ThemeMode = themeModeFlow.value

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putInt(KEY_THEME_MODE, mode.ordinal).apply()
        themeModeFlow.value = mode
    }

    companion object {
        private const val KEY_LAST_SYNC = "last_sync_timestamp"
        private const val KEY_SYNC_INTERVAL = "sync_interval_minutes"
        private const val KEY_THEME_MODE = "theme_mode"
        const val DEFAULT_SYNC_INTERVAL_MINUTES = 30L
    }
}
