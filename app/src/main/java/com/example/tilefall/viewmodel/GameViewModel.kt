package com.example.tilefall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tilefall.data.GamePreferences
import com.example.tilefall.engine.GameEngine
import com.example.tilefall.model.Difficulty
import com.example.tilefall.model.GameState
import com.example.tilefall.model.GameStatus
import com.example.tilefall.sound.SoundHapticsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application)
    val soundHapticsManager = SoundHapticsManager(application)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var timeSinceLastSpawnMs: Long = 0L
    private var nextTileId: Long = 1L
    private var density: Float = 2.5f

    init {
        viewModelScope.launch {
            val userSettings = preferences.userSettingsFlow.first()
            soundHapticsManager.isSoundEnabled = userSettings.soundEnabled
            soundHapticsManager.isHapticsEnabled = userSettings.hapticsEnabled

            _gameState.update {
                it.copy(
                    bestScore = userSettings.bestScore,
                    highestStreak = userSettings.highestStreak,
                    difficulty = userSettings.lastDifficulty
                )
            }
        }
    }

    fun onPlayfieldSizeChanged(width: Float, height: Float, screenDensity: Float) {
        density = screenDensity
        _gameState.update {
            it.copy(
                playfieldWidth = width,
                playfieldHeight = height,
                dangerLineY = height * 0.88f
            )
        }
    }

    fun startGame(difficulty: Difficulty) {
        _gameState.update {
            GameState(
                status = GameStatus.PLAYING,
                difficulty = difficulty,
                score = 0,
                bestScore = it.bestScore,
                lives = 3,
                maxLives = 3,
                streak = 0,
                highestStreak = 0,
                tilesTapped = 0,
                tilesMissed = 0,
                tiles = emptyList(),
                particles = emptyList(),
                playfieldWidth = it.playfieldWidth,
                playfieldHeight = it.playfieldHeight,
                dangerLineY = if (it.dangerLineY > 0f) it.dangerLineY else it.playfieldHeight * 0.88f
            )
        }

        timeSinceLastSpawnMs = 0L
        startGameLoop()

        viewModelScope.launch {
            val settings = preferences.userSettingsFlow.first()
            soundHapticsManager.isSoundEnabled = settings.soundEnabled
            soundHapticsManager.isHapticsEnabled = settings.hapticsEnabled
            _gameState.update {
                it.copy(bestScore = maxOf(it.bestScore, settings.bestScore))
            }
        }
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var lastTimeNanos = System.nanoTime()

            while (isActive) {
                delay(16L) // ~60 FPS update rate

                val currentTimeNanos = System.nanoTime()
                val deltaSec = (currentTimeNanos - lastTimeNanos) / 1_000_000_000f
                lastTimeNanos = currentTimeNanos

                val currentState = _gameState.value
                if (currentState.status != GameStatus.PLAYING) {
                    continue
                }

                // 1. Spawning logic
                timeSinceLastSpawnMs += (deltaSec * 1000f).toLong()
                val currentSpawnInterval = GameEngine.calculateSpawnIntervalMs(
                    currentState.difficulty,
                    currentState.score
                )

                if (timeSinceLastSpawnMs >= currentSpawnInterval) {
                    val activeTiles = currentState.tiles.filter { !it.isTapped && !it.isMissed }
                    if (activeTiles.size < currentState.difficulty.maxActiveTiles && currentState.playfieldWidth > 0f) {
                        val tileHeightPx = 110f * density
                        val minGapPx = tileHeightPx * 1.35f
                        val availableLanes = GameEngine.getAvailableLanes(currentState.tiles, minGapPx)

                        if (availableLanes.isNotEmpty()) {
                            val selectedLane = availableLanes.random()
                            val speed = GameEngine.calculateSpeed(
                                currentState.difficulty,
                                currentState.score,
                                density
                            )
                            val colorIdx = Random.nextInt(6)

                            val newTile = GameEngine.createTile(
                                id = nextTileId++,
                                lane = selectedLane,
                                playfieldWidth = currentState.playfieldWidth,
                                tileHeightPx = tileHeightPx,
                                speedPxPerSec = speed,
                                colorIndex = colorIdx
                            )

                            _gameState.update { it.copy(tiles = it.tiles + newTile) }
                            timeSinceLastSpawnMs = 0L
                        }
                    }
                }

                // 2. Physics & collision frame step
                val frameResult = GameEngine.updateFrame(
                    _gameState.value,
                    deltaSec
                )

                _gameState.value = frameResult.updatedState

                if (frameResult.missedTilesCount > 0) {
                    soundHapticsManager.playMiss()
                }

                if (frameResult.newGameOver) {
                    soundHapticsManager.playGameOver()
                    saveGameOverStats(frameResult.updatedState)
                    gameLoopJob?.cancel()
                    break
                }
            }
        }
    }

    fun handleTap(tapX: Float, tapY: Float) {
        val currentState = _gameState.value
        if (currentState.status != GameStatus.PLAYING) return

        val (newState, result) = GameEngine.processTap(
            currentState,
            tapX,
            tapY,
            slopPx = 28f * (density / 2.5f)
        )

        if (result is GameEngine.TapResult.Hit) {
            _gameState.value = newState
            if (result.newStreak > 0 && result.newStreak % 5 == 0) {
                soundHapticsManager.playStreak()
            } else {
                soundHapticsManager.playTap()
            }
        }
    }

    fun pauseGame() {
        if (_gameState.value.status == GameStatus.PLAYING) {
            _gameState.update { it.copy(status = GameStatus.PAUSED) }
        }
    }

    fun resumeGame() {
        if (_gameState.value.status == GameStatus.PAUSED) {
            _gameState.update { it.copy(status = GameStatus.PLAYING) }
        }
    }

    fun restartGame() {
        startGame(_gameState.value.difficulty)
    }

    private fun saveGameOverStats(finalState: GameState) {
        viewModelScope.launch {
            preferences.updateHighScoreAndStreak(finalState.score, finalState.highestStreak)
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}
