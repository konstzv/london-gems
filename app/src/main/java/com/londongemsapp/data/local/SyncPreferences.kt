package com.londongemsapp.data.local

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

    companion object {
        private const val KEY_LAST_SYNC = "last_sync_timestamp"
    }
}
