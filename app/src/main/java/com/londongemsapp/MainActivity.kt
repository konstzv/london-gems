package com.londongemsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.londongemsapp.data.local.SyncPreferences
import com.londongemsapp.presentation.navigation.AppNavigation
import com.londongemsapp.presentation.theme.LondonGemsTheme
import com.londongemsapp.presentation.theme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var syncPreferences: SyncPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by syncPreferences.getThemeMode()
                .collectAsState(initial = syncPreferences.getThemeModeValue())

            LondonGemsTheme(
                darkTheme = when (themeMode) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    ThemeMode.SYSTEM -> null
                }
            ) {
                AppNavigation()
            }
        }
    }
}
