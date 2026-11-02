package com.example.tilefall.model

enum class Difficulty(
    val title: String,
    val description: String,
    val baseSpeedDp: Float,
    val speedIncrementDp: Float,
    val maxSpeedDp: Float,
    val baseSpawnIntervalMs: Long,
    val minSpawnIntervalMs: Long,
    val spawnDecrementMs: Long,
    val maxActiveTiles: Int
) {
    EASY(
        title = "Easy",
        description = "Slower speed, relaxed spawns",
        baseSpeedDp = 260f,
        speedIncrementDp = 12f,
        maxSpeedDp = 500f,
        baseSpawnIntervalMs = 1200L,
        minSpawnIntervalMs = 800L,
        spawnDecrementMs = 30L,
        maxActiveTiles = 3
    ),
    MEDIUM(
        title = "Medium",
        description = "Balanced pace, standard challenge",
        baseSpeedDp = 380f,
        speedIncrementDp = 18f,
        maxSpeedDp = 700f,
        baseSpawnIntervalMs = 950L,
        minSpawnIntervalMs = 550L,
        spawnDecrementMs = 40L,
        maxActiveTiles = 5
    ),
    HARD(
        title = "Hard",
        description = "High velocity, intense spawning",
        baseSpeedDp = 520f,
        speedIncrementDp = 25f,
        maxSpeedDp = 950f,
        baseSpawnIntervalMs = 750L,
        minSpawnIntervalMs = 400L,
        spawnDecrementMs = 50L,
        maxActiveTiles = 7
    )
}
