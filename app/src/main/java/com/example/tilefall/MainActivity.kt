package com.example.tilefall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.tilefall.ui.navigation.TileFallNavGraph
import com.example.tilefall.ui.theme.TileFallTheme
import com.example.tilefall.viewmodel.GameViewModel
import com.example.tilefall.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by settingsViewModel.settings.collectAsState()

            TileFallTheme(themeMode = settings.themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    TileFallNavGraph(
                        navController = navController,
                        settingsViewModel = settingsViewModel,
                        gameViewModel = gameViewModel
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        gameViewModel.pauseGame()
    }
}
