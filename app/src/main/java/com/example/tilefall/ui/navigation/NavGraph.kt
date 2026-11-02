package com.example.tilefall.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tilefall.data.UserSettings
import com.example.tilefall.model.Difficulty
import com.example.tilefall.ui.screens.AboutScreen
import com.example.tilefall.ui.screens.GameScreen
import com.example.tilefall.ui.screens.HomeScreen
import com.example.tilefall.ui.screens.ResultScreen
import com.example.tilefall.ui.screens.SettingsScreen
import com.example.tilefall.ui.screens.SplashScreen
import com.example.tilefall.viewmodel.GameViewModel
import com.example.tilefall.viewmodel.SettingsViewModel

@Composable
fun TileFallNavGraph(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    gameViewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val settings by settingsViewModel.settings.collectAsState()
    var selectedDifficulty by remember(settings.lastDifficulty) {
        mutableStateOf(settings.lastDifficulty)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier.fillMaxSize(),
        enterTransition = { fadeIn(tween(250)) },
        exitTransition = { fadeOut(tween(250)) }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                settings = settings,
                selectedDifficulty = selectedDifficulty,
                onDifficultySelected = { diff ->
                    selectedDifficulty = diff
                    settingsViewModel.setLastDifficulty(diff)
                },
                onPlayClicked = {
                    navController.navigate(Screen.Game.createRoute(selectedDifficulty))
                },
                onSettingsClicked = {
                    navController.navigate(Screen.Settings.route)
                },
                onAboutClicked = {
                    navController.navigate(Screen.About.route)
                }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("difficulty") {
                    type = NavType.StringType
                    defaultValue = Difficulty.MEDIUM.name
                }
            )
        ) { backStackEntry ->
            val diffName = backStackEntry.arguments?.getString("difficulty") ?: Difficulty.MEDIUM.name
            val difficulty = try {
                Difficulty.valueOf(diffName)
            } catch (_: Exception) {
                Difficulty.MEDIUM
            }

            GameScreen(
                viewModel = gameViewModel,
                themeMode = settings.themeMode,
                difficulty = difficulty,
                onGameOver = { score, bestScore, tapped, missed, streak, diff ->
                    navController.navigate(
                        Screen.Result.createRoute(
                            score = score,
                            bestScore = maxOf(bestScore, score),
                            tapped = tapped,
                            missed = missed,
                            streak = streak,
                            difficulty = diff
                        )
                    ) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType; defaultValue = 0 },
                navArgument("bestScore") { type = NavType.IntType; defaultValue = 0 },
                navArgument("tapped") { type = NavType.IntType; defaultValue = 0 },
                navArgument("missed") { type = NavType.IntType; defaultValue = 0 },
                navArgument("streak") { type = NavType.IntType; defaultValue = 0 },
                navArgument("difficulty") { type = NavType.StringType; defaultValue = Difficulty.MEDIUM.name }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val bestScore = backStackEntry.arguments?.getInt("bestScore") ?: 0
            val tapped = backStackEntry.arguments?.getInt("tapped") ?: 0
            val missed = backStackEntry.arguments?.getInt("missed") ?: 0
            val streak = backStackEntry.arguments?.getInt("streak") ?: 0
            val diffStr = backStackEntry.arguments?.getString("difficulty") ?: Difficulty.MEDIUM.name
            val diff = try {
                Difficulty.valueOf(diffStr)
            } catch (_: Exception) {
                Difficulty.MEDIUM
            }

            ResultScreen(
                score = score,
                bestScore = bestScore,
                tilesTapped = tapped,
                tilesMissed = missed,
                highestStreak = streak,
                difficulty = diff,
                onPlayAgain = {
                    navController.navigate(Screen.Game.createRoute(diff)) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settings = settings,
                onThemeChanged = { settingsViewModel.setThemeMode(it) },
                onSoundChanged = { settingsViewModel.setSoundEnabled(it) },
                onHapticsChanged = { settingsViewModel.setHapticsEnabled(it) },
                onResetScores = { settingsViewModel.resetBestScore() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
