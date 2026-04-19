package com.londongemsapp.presentation.settings

import androidx.lifecycle.ViewModel
import com.londongemsapp.data.local.SyncPreferences
import com.londongemsapp.data.worker.SyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import androidx.lifecycle.viewModelScope

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val syncPreferences: SyncPreferences,
    private val syncScheduler: SyncScheduler
) : ViewModel() {

    val syncIntervalMinutes: StateFlow<Long> = syncPreferences.getSyncIntervalMinutes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = syncPreferences.getSyncIntervalMinutesValue()
        )

    fun setSyncInterval(minutes: Long) {
        syncPreferences.setSyncIntervalMinutes(minutes)
        syncScheduler.schedule(minutes)
    }
}
