package com.example.tilefall.model

enum class GameStatus {
    READY,
    PLAYING,
    PAUSED,
    GAME_OVER
}

data class GameState(
    val status: GameStatus = GameStatus.READY,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val score: Int = 0,
    val bestScore: Int = 0,
    val lives: Int = 3,
    val maxLives: Int = 3,
    val streak: Int = 0,
    val highestStreak: Int = 0,
    val tilesTapped: Int = 0,
    val tilesMissed: Int = 0,
    val tiles: List<Tile> = emptyList(),
    val particles: List<PopParticle> = emptyList(),
    val shakeOffsetX: Float = 0f,
    val shakeOffsetY: Float = 0f,
    val dangerFlashAlpha: Float = 0f,
    val playfieldWidth: Float = 0f,
    val playfieldHeight: Float = 0f,
    val dangerLineY: Float = 0f,
    val isNewHighScore: Boolean = false
) {
    val streakMultiplier: Float
        get() = 1.0f + (streak / 5) * 0.2f
}
