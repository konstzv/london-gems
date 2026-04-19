package com.londongemsapp.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
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
            // Sync frequency
            ListItem(
                headlineContent = { Text("Sync frequency") },
                supportingContent = { Text("Every 30 minutes") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Sync,
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable { /* placeholder */ }
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
                supportingContent = { Text("London Gems v1.0.0") },
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
