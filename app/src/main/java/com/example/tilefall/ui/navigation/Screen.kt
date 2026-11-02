package com.example.tilefall.ui.navigation

import com.example.tilefall.model.Difficulty

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Game : Screen("game/{difficulty}") {
        fun createRoute(difficulty: Difficulty): String = "game/${difficulty.name}"
    }
    object Result : Screen("result/{score}/{bestScore}/{tapped}/{missed}/{streak}/{difficulty}") {
        fun createRoute(
            score: Int,
            bestScore: Int,
            tapped: Int,
            missed: Int,
            streak: Int,
            difficulty: Difficulty
        ): String = "result/$score/$bestScore/$tapped/$missed/$streak/${difficulty.name}"
    }
    object Settings : Screen("settings")
    object About : Screen("about")
}
