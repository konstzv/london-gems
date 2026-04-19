package com.londongemsapp.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.londongemsapp.BuildConfig
import com.londongemsapp.presentation.theme.ThemeMode

private val SYNC_INTERVAL_OPTIONS = listOf(15L, 30L, 60L, 120L, 360L)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val syncInterval by viewModel.syncIntervalMinutes.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    var showIntervalPicker by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }

    if (showIntervalPicker) {
        AlertDialog(
            onDismissRequest = { showIntervalPicker = false },
            title = { Text("Sync frequency") },
            text = {
                Column {
                    SYNC_INTERVAL_OPTIONS.forEach { minutes ->
                        val label = when {
                            minutes < 60 -> "Every $minutes minutes"
                            minutes == 60L -> "Every hour"
                            else -> "Every ${minutes / 60} hours"
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                viewModel.setSyncInterval(minutes)
                                showIntervalPicker = false
                            }
                        ) {
                            RadioButton(
                                selected = syncInterval == minutes,
                                onClick = {
                                    viewModel.setSyncInterval(minutes)
                                    showIntervalPicker = false
                                }
                            )
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIntervalPicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showThemePicker) {
        AlertDialog(
            onDismissRequest = { showThemePicker = false },
            title = { Text("Theme") },
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        val label = when (mode) {
                            ThemeMode.SYSTEM -> "System default"
                            ThemeMode.LIGHT -> "Light"
                            ThemeMode.DARK -> "Dark"
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                viewModel.setThemeMode(mode)
                                showThemePicker = false
                            }
                        ) {
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = {
                                    viewModel.setThemeMode(mode)
                                    showThemePicker = false
                                }
                            )
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ListItem(
                headlineContent = { Text("Sync frequency") },
                supportingContent = {
                    val label = when {
                        syncInterval < 60 -> "Every $syncInterval minutes"
                        syncInterval == 60L -> "Every hour"
                        else -> "Every ${syncInterval / 60} hours"
                    }
                    Text(label)
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Sync,
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable { showIntervalPicker = true }
            )

            HorizontalDivider()

            ListItem(
                headlineContent = { Text("Theme") },
                supportingContent = {
                    Text(when (themeMode) {
                        ThemeMode.SYSTEM -> "System default"
                        ThemeMode.LIGHT -> "Light"
                        ThemeMode.DARK -> "Dark"
                    })
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.DarkMode,
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable { showThemePicker = true }
            )

            HorizontalDivider()

            // Clear cache
            ListItem(
                headlineContent = { Text("Clear cache") },
                supportingContent = { Text("Remove all cached recommendations") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable { /* placeholder */ }
            )

            HorizontalDivider()

            // About
            ListItem(
                headlineContent = { Text("About") },
                supportingContent = { Text("London Gems v${BuildConfig.VERSION_NAME}") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable { /* placeholder */ }
            )
        }
    }
}
