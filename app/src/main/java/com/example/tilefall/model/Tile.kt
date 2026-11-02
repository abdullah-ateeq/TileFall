package com.example.tilefall.model

data class Tile(
    val id: Long,
    val lane: Int,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val speed: Float,
    val colorIndex: Int,
    val isTapped: Boolean = false,
    val isMissed: Boolean = false,
    val popProgress: Float = 0f,
    val scoreEarned: Int = 0,
    val speedBonusEarned: Int = 0
) {
    val isFinished: Boolean
        get() = isMissed || (isTapped && popProgress >= 1f)

    fun contains(tapX: Float, tapY: Float, slopPx: Float = 20f): Boolean {
        return tapX >= (x - slopPx) &&
                tapX <= (x + width + slopPx) &&
                tapY >= (y - slopPx) &&
                tapY <= (y + height + slopPx)
    }
}

data class PopParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val radius: Float,
    val alpha: Float,
    val colorIndex: Int,
    val life: Float // 1f down to 0f
)
