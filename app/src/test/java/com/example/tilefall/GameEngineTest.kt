package com.example.tilefall

import com.example.tilefall.engine.GameEngine
import com.example.tilefall.model.Difficulty
import com.example.tilefall.model.GameState
import com.example.tilefall.model.GameStatus
import com.example.tilefall.model.Tile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameEngineTest {

    @Test
    fun testTileMovement() {
        val initialY = 100f
        val speed = 400f // 400 px/sec
        val deltaSec = 0.016f // 16 ms

        val tile = Tile(
            id = 1L,
            lane = 0,
            x = 20f,
            y = initialY,
            width = 80f,
            height = 120f,
            speed = speed,
            colorIndex = 0
        )

        val state = GameState(
            status = GameStatus.PLAYING,
            tiles = listOf(tile),
            dangerLineY = 800f
        )

        val result = GameEngine.updateFrame(state, deltaSec, Random(0))
        val updatedTile = result.updatedState.tiles.first()

        val expectedY = initialY + speed * deltaSec
        assertEquals(expectedY, updatedTile.y, 0.01f)
    }

    @Test
    fun testTileBoundsAndContainment() {
        val tile = Tile(
            id = 1L,
            lane = 1,
            x = 100f,
            y = 200f,
            width = 80f,
            height = 120f,
            speed = 300f,
            colorIndex = 1
        )

        // Point strictly inside
        assertTrue(tile.contains(120f, 250f, slopPx = 0f))
        assertTrue(tile.contains(100f, 200f, slopPx = 0f))
        assertTrue(tile.contains(180f, 320f, slopPx = 0f))

        // Point strictly outside with 0 slop
        assertFalse(tile.contains(90f, 250f, slopPx = 0f))
        assertFalse(tile.contains(190f, 250f, slopPx = 0f))
        assertFalse(tile.contains(120f, 190f, slopPx = 0f))
        assertFalse(tile.contains(120f, 330f, slopPx = 0f))

        // Point inside slop tolerance margin (e.g. 20px)
        assertTrue(tile.contains(90f, 250f, slopPx = 20f))
        assertTrue(tile.contains(195f, 250f, slopPx = 20f))
    }

    @Test
    fun testTapDetectionPrioritizesLowestTile() {
        val tileUpper = Tile(
            id = 1L,
            lane = 0,
            x = 50f,
            y = 100f,
            width = 100f,
            height = 100f,
            speed = 200f,
            colorIndex = 0
        )
        val tileLower = Tile(
            id = 2L,
            lane = 0,
            x = 50f,
            y = 180f,
            width = 100f,
            height = 100f,
            speed = 200f,
            colorIndex = 1
        )

        val tiles = listOf(tileUpper, tileLower)

        // Tap in overlapping region Y=190
        val tapped = GameEngine.findTappedTile(tiles, 80f, 190f, slopPx = 0f)
        assertNotNull(tapped)
        assertEquals(2L, tapped?.id) // Lower tile selected!
    }

    @Test
    fun testMissAndLifeCalculation() {
        val tile = Tile(
            id = 1L,
            lane = 0,
            x = 10f,
            y = 690f,
            width = 80f,
            height = 100f,
            speed = 500f,
            colorIndex = 0
        )

        val state = GameState(
            status = GameStatus.PLAYING,
            lives = 3,
            streak = 8,
            tilesMissed = 0,
            tiles = listOf(tile),
            dangerLineY = 700f // tile.y + height = 790 >= 700 -> crosses danger line!
        )

        val result = GameEngine.updateFrame(state, deltaSeconds = 0.016f, Random(0))

        assertEquals(1, result.missedTilesCount)
        assertEquals(2, result.updatedState.lives)
        assertEquals(0, result.updatedState.streak) // Streak reset on miss
        assertEquals(1, result.updatedState.tilesMissed)
        assertTrue(result.updatedState.tiles.isEmpty())
        assertFalse(result.newGameOver)
    }

    @Test
    fun testScoreAndStreakCalculation() {
        // Base score: 100
        val (scoreZeroStreak, bonusZero) = GameEngine.calculateScore(100, streak = 2, isTopHalfTap = false)
        assertEquals(100, scoreZeroStreak)
        assertEquals(0, bonusZero)

        // Streak 5 -> 1.2x multiplier -> 120 points
        val (scoreStreak5, _) = GameEngine.calculateScore(100, streak = 5, isTopHalfTap = false)
        assertEquals(120, scoreStreak5)

        // Streak 10 with speed bonus (top half tap) -> 1.4x (140) + 50 = 190
        val (scoreTopHalf, speedBonus) = GameEngine.calculateScore(100, streak = 10, isTopHalfTap = true)
        assertEquals(190, scoreTopHalf)
        assertEquals(50, speedBonus)
    }

    @Test
    fun testProcessTapUpdatesState() {
        val tile = Tile(
            id = 5L,
            lane = 1,
            x = 100f,
            y = 450f, // in bottom half (> 800 * 0.45 = 360)
            width = 80f,
            height = 100f,
            speed = 200f,
            colorIndex = 2
        )

        val initialState = GameState(
            status = GameStatus.PLAYING,
            score = 200,
            bestScore = 500,
            streak = 4,
            tilesTapped = 2,
            tiles = listOf(tile),
            dangerLineY = 800f
        )

        val (newState, result) = GameEngine.processTap(
            initialState,
            tapX = 140f,
            tapY = 480f,
            slopPx = 10f,
            random = Random(0)
        )

        assertTrue(result is GameEngine.TapResult.Hit)
        val hit = result as GameEngine.TapResult.Hit
        assertEquals(5L, hit.tappedTile.id)
        assertEquals(5, newState.streak)
        assertEquals(3, newState.tilesTapped)
        assertEquals(0, hit.speedBonus)
        assertEquals(120, hit.pointsAwarded)
        assertEquals(320, newState.score) // 200 + 120 (streak 5)
        assertEquals(5, newState.highestStreak)
        assertTrue(newState.tiles.first().isTapped)
    }

    @Test
    fun testDoubleTappingIgnored() {
        val tile = Tile(
            id = 7L,
            lane = 0,
            x = 50f,
            y = 200f,
            width = 80f,
            height = 100f,
            speed = 200f,
            colorIndex = 1,
            isTapped = true // Already tapped!
        )

        val state = GameState(
            status = GameStatus.PLAYING,
            score = 100,
            tiles = listOf(tile),
            dangerLineY = 800f
        )

        val (newState, result) = GameEngine.processTap(
            state,
            tapX = 70f,
            tapY = 250f,
            slopPx = 10f
        )

        assertTrue(result is GameEngine.TapResult.Miss)
        assertEquals(100, newState.score) // Score unchanged!
    }

    @Test
    fun testDifficultyProgression() {
        val easy = Difficulty.EASY
        val density = 2.0f

        val initialSpeed = GameEngine.calculateSpeed(easy, score = 0, density = density)
        val higherSpeed = GameEngine.calculateSpeed(easy, score = 2500, density = density)
        assertTrue(higherSpeed > initialSpeed)

        // Clamped at max speed
        val maxSpeed = GameEngine.calculateSpeed(easy, score = 999999, density = density)
        assertEquals(easy.maxSpeedDp * density, maxSpeed, 0.001f)

        // Spawn interval decreases
        val initialSpawn = GameEngine.calculateSpawnIntervalMs(easy, score = 0)
        val fastSpawn = GameEngine.calculateSpawnIntervalMs(easy, score = 2000)
        assertTrue(fastSpawn < initialSpawn)

        val clampedSpawn = GameEngine.calculateSpawnIntervalMs(easy, score = 999999)
        assertEquals(easy.minSpawnIntervalMs, clampedSpawn)
    }

    @Test
    fun testGameOverConditions() {
        val tile = Tile(
            id = 10L,
            lane = 0,
            x = 20f,
            y = 750f,
            width = 80f,
            height = 100f,
            speed = 300f,
            colorIndex = 0
        )

        val state = GameState(
            status = GameStatus.PLAYING,
            lives = 1, // Only 1 life left
            tiles = listOf(tile),
            dangerLineY = 700f
        )

        val result = GameEngine.updateFrame(state, deltaSeconds = 0.016f, Random(0))

        assertEquals(0, result.updatedState.lives)
        assertEquals(GameStatus.GAME_OVER, result.updatedState.status)
        assertTrue(result.newGameOver)
    }

    @Test
    fun testFairSpawningPreventsLaneOverlap() {
        val tileAtTop = Tile(
            id = 1L,
            lane = 2,
            x = 200f,
            y = 30f, // Near the top
            width = 80f,
            height = 100f,
            speed = 200f,
            colorIndex = 0
        )

        val tiles = listOf(tileAtTop)
        val minGapPx = 140f

        // Lane 2 has a tile at y=30 < 140 -> cannot spawn in lane 2
        assertFalse(GameEngine.canSpawnInLane(lane = 2, tiles = tiles, minGapPx = minGapPx))

        // Lanes 0, 1, 3 are empty -> can spawn
        assertTrue(GameEngine.canSpawnInLane(lane = 0, tiles = tiles, minGapPx = minGapPx))
        assertTrue(GameEngine.canSpawnInLane(lane = 1, tiles = tiles, minGapPx = minGapPx))
        assertTrue(GameEngine.canSpawnInLane(lane = 3, tiles = tiles, minGapPx = minGapPx))

        val available = GameEngine.getAvailableLanes(tiles, minGapPx, laneCount = 4)
        assertEquals(listOf(0, 1, 3), available)
    }
}
