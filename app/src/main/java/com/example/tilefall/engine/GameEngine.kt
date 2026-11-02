package com.example.tilefall.engine

import com.example.tilefall.model.Difficulty
import com.example.tilefall.model.GameState
import com.example.tilefall.model.GameStatus
import com.example.tilefall.model.PopParticle
import com.example.tilefall.model.Tile
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

object GameEngine {

    const val LANE_COUNT = 4
    const val POP_DURATION_SECONDS = 0.18f
    const val PARTICLE_COUNT = 12

    sealed class TapResult {
        data class Hit(
            val tappedTile: Tile,
            val pointsAwarded: Int,
            val speedBonus: Int,
            val newStreak: Int,
            val particles: List<PopParticle>
        ) : TapResult()

        object Miss : TapResult()
    }

    data class FrameUpdateResult(
        val updatedState: GameState,
        val missedTilesCount: Int,
        val newGameOver: Boolean
    )

    fun calculateSpeed(difficulty: Difficulty, score: Int, density: Float): Float {
        val scoreSteps = score / 500
        val speedDp = minOf(
            difficulty.maxSpeedDp,
            difficulty.baseSpeedDp + scoreSteps * difficulty.speedIncrementDp
        )
        return speedDp * density
    }

    fun calculateSpawnIntervalMs(difficulty: Difficulty, score: Int): Long {
        val scoreSteps = score / 400
        return maxOf(
            difficulty.minSpawnIntervalMs,
            difficulty.baseSpawnIntervalMs - scoreSteps * difficulty.spawnDecrementMs
        )
    }

    fun calculateScore(baseScore: Int, streak: Int, isTopHalfTap: Boolean): Pair<Int, Int> {
        val streakMultiplier = 1.0f + (streak / 5) * 0.2f
        val speedBonus = if (isTopHalfTap) 50 else 0
        val points = (baseScore * streakMultiplier).toInt() + speedBonus
        return Pair(points, speedBonus)
    }

    fun findTappedTile(tiles: List<Tile>, tapX: Float, tapY: Float, slopPx: Float = 24f): Tile? {
        // Look for active tiles containing tap coordinate.
        // If multiple match, prioritize the lowest one (largest Y, nearest to danger line)
        return tiles
            .filter { !it.isTapped && !it.isMissed && it.contains(tapX, tapY, slopPx) }
            .maxByOrNull { it.y }
    }

    fun canSpawnInLane(lane: Int, tiles: List<Tile>, minGapPx: Float): Boolean {
        val highestInLane = tiles
            .filter { it.lane == lane && !it.isFinished }
            .minByOrNull { it.y }

        return highestInLane == null || highestInLane.y >= minGapPx
    }

    fun getAvailableLanes(tiles: List<Tile>, minGapPx: Float, laneCount: Int = LANE_COUNT): List<Int> {
        return (0 until laneCount).filter { canSpawnInLane(it, tiles, minGapPx) }
    }

    fun createTile(
        id: Long,
        lane: Int,
        playfieldWidth: Float,
        laneCount: Int = LANE_COUNT,
        tileHeightPx: Float,
        speedPxPerSec: Float,
        colorIndex: Int
    ): Tile {
        val laneWidth = playfieldWidth / laneCount
        val tileWidth = laneWidth * 0.88f
        val x = lane * laneWidth + (laneWidth - tileWidth) / 2f
        val y = -tileHeightPx

        return Tile(
            id = id,
            lane = lane,
            x = x,
            y = y,
            width = tileWidth,
            height = tileHeightPx,
            speed = speedPxPerSec,
            colorIndex = colorIndex
        )
    }

    fun generateParticles(
        centerX: Float,
        centerY: Float,
        colorIndex: Int,
        count: Int = PARTICLE_COUNT,
        random: Random = Random
    ): List<PopParticle> {
        val list = mutableListOf<PopParticle>()
        for (i in 0 until count) {
            val angle = random.nextDouble(0.0, Math.PI * 2)
            val speed = random.nextFloat() * 260f + 140f
            val vx = (cos(angle) * speed).toFloat()
            val vy = (sin(angle) * speed).toFloat()
            val radius = random.nextFloat() * 6f + 4f
            list.add(
                PopParticle(
                    x = centerX,
                    y = centerY,
                    vx = vx,
                    vy = vy,
                    radius = radius,
                    alpha = 1f,
                    colorIndex = colorIndex,
                    life = 1f
                )
            )
        }
        return list
    }

    fun processTap(
        state: GameState,
        tapX: Float,
        tapY: Float,
        slopPx: Float = 24f,
        random: Random = Random
    ): Pair<GameState, TapResult> {
        if (state.status != GameStatus.PLAYING) {
            return Pair(state, TapResult.Miss)
        }

        val tappedTile = findTappedTile(state.tiles, tapX, tapY, slopPx)
            ?: return Pair(state, TapResult.Miss)

        val newStreak = state.streak + 1
        val isTopHalf = tappedTile.y < (state.dangerLineY * 0.45f)
        val (pointsEarned, speedBonus) = calculateScore(100, newStreak, isTopHalf)

        val updatedScore = state.score + pointsEarned
        val updatedBestScore = maxOf(state.bestScore, updatedScore)
        val updatedHighestStreak = maxOf(state.highestStreak, newStreak)

        val updatedTiles = state.tiles.map { tile ->
            if (tile.id == tappedTile.id) {
                tile.copy(
                    isTapped = true,
                    popProgress = 0f,
                    scoreEarned = pointsEarned,
                    speedBonusEarned = speedBonus
                )
            } else {
                tile
            }
        }

        val newParticles = generateParticles(
            centerX = tappedTile.x + tappedTile.width / 2f,
            centerY = tappedTile.y + tappedTile.height / 2f,
            colorIndex = tappedTile.colorIndex,
            random = random
        )

        val newState = state.copy(
            score = updatedScore,
            bestScore = updatedBestScore,
            streak = newStreak,
            highestStreak = updatedHighestStreak,
            tilesTapped = state.tilesTapped + 1,
            tiles = updatedTiles,
            particles = state.particles + newParticles,
            isNewHighScore = updatedScore > state.bestScore
        )

        return Pair(
            newState,
            TapResult.Hit(
                tappedTile = tappedTile,
                pointsAwarded = pointsEarned,
                speedBonus = speedBonus,
                newStreak = newStreak,
                particles = newParticles
            )
        )
    }

    fun updateFrame(
        state: GameState,
        deltaSeconds: Float,
        random: Random = Random
    ): FrameUpdateResult {
        if (state.status != GameStatus.PLAYING) {
            return FrameUpdateResult(state, 0, false)
        }

        val clampedDelta = minOf(deltaSeconds, 0.05f) // avoid huge jumps if frame stalls
        var missedCount = 0
        val remainingTiles = mutableListOf<Tile>()

        for (tile in state.tiles) {
            if (tile.isTapped) {
                val newProgress = tile.popProgress + (clampedDelta / POP_DURATION_SECONDS)
                if (newProgress < 1f) {
                    remainingTiles.add(tile.copy(popProgress = newProgress))
                }
            } else {
                val newY = tile.y + tile.speed * clampedDelta
                val reachedDanger = (newY + tile.height) >= state.dangerLineY

                if (reachedDanger) {
                    missedCount++
                } else {
                    remainingTiles.add(tile.copy(y = newY))
                }
            }
        }

        val updatedLives = maxOf(0, state.lives - missedCount)
        val isGameOver = updatedLives <= 0
        val updatedMissedTotal = state.tilesMissed + missedCount

        // Update particles
        val updatedParticles = state.particles.mapNotNull { p ->
            val newLife = p.life - (clampedDelta / 0.35f)
            if (newLife > 0f) {
                p.copy(
                    x = p.x + p.vx * clampedDelta,
                    y = p.y + p.vy * clampedDelta,
                    life = newLife,
                    alpha = newLife.coerceIn(0f, 1f)
                )
            } else {
                null
            }
        }

        // Screen shake and danger flash
        val newShakeOffsetX = if (missedCount > 0) {
            (random.nextFloat() * 24f - 12f)
        } else {
            state.shakeOffsetX * 0.82f
        }

        val newShakeOffsetY = if (missedCount > 0) {
            (random.nextFloat() * 24f - 12f)
        } else {
            state.shakeOffsetY * 0.82f
        }

        val newDangerAlpha = if (missedCount > 0) {
            0.65f
        } else {
            maxOf(0f, state.dangerFlashAlpha - clampedDelta * 2.8f)
        }

        val updatedStreak = if (missedCount > 0) 0 else state.streak

        val updatedState = state.copy(
            status = if (isGameOver) GameStatus.GAME_OVER else state.status,
            lives = updatedLives,
            streak = updatedStreak,
            tilesMissed = updatedMissedTotal,
            tiles = remainingTiles,
            particles = updatedParticles,
            shakeOffsetX = newShakeOffsetX,
            shakeOffsetY = newShakeOffsetY,
            dangerFlashAlpha = newDangerAlpha
        )

        return FrameUpdateResult(
            updatedState = updatedState,
            missedTilesCount = missedCount,
            newGameOver = isGameOver
        )
    }
}
