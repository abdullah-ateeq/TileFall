package com.example.tilefall.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.tilefall.engine.GameEngine
import com.example.tilefall.model.GameState
import com.example.tilefall.model.Tile
import com.example.tilefall.ui.theme.CrimsonDanger
import com.example.tilefall.ui.theme.TileGradients

@Composable
fun PlayfieldCanvas(
    state: GameState,
    isDarkTheme: Boolean,
    onTap: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dangerGlow")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(state.status) {
                detectTapGestures { offset ->
                    onTap(offset.x, offset.y)
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val dangerY = state.dangerLineY.takeIf { it > 0f } ?: (height * 0.88f)

        // 1. Draw subtle lane divider guidelines
        val laneWidth = width / GameEngine.LANE_COUNT
        val laneLineColor = if (isDarkTheme) Color(0x18FFFFFF) else Color(0x14000000)
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 16f), 0f)

        for (i in 1 until GameEngine.LANE_COUNT) {
            val laneX = i * laneWidth
            drawLine(
                color = laneLineColor,
                start = Offset(laneX, 0f),
                end = Offset(laneX, dangerY),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = dashEffect
            )
        }

        // 2. Draw danger zone gradient below danger line
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    CrimsonDanger.copy(alpha = 0.18f * pulseGlow),
                    CrimsonDanger.copy(alpha = 0.05f)
                ),
                startY = dangerY,
                endY = height
            ),
            topLeft = Offset(0f, dangerY),
            size = Size(width, height - dangerY)
        )

        // 3. Draw danger warning line with glow
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    CrimsonDanger.copy(alpha = 0.3f),
                    CrimsonDanger.copy(alpha = 0.95f),
                    CrimsonDanger.copy(alpha = 0.3f)
                )
            ),
            start = Offset(0f, dangerY),
            end = Offset(width, dangerY),
            strokeWidth = 3.dp.toPx()
        )

        // Danger line soft ambient glow
        drawLine(
            color = CrimsonDanger.copy(alpha = 0.4f * pulseGlow),
            start = Offset(0f, dangerY),
            end = Offset(width, dangerY),
            strokeWidth = 8.dp.toPx()
        )

        // 4. Draw falling and popping tiles
        val cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())

        for (tile in state.tiles) {
            val theme = TileGradients[tile.colorIndex % TileGradients.size]

            if (tile.isTapped) {
                // Popping animation: expands and fades
                val popProgress = tile.popProgress.coerceIn(0f, 1f)
                val scaleMultiplier = 1f + 0.35f * popProgress
                val alpha = (1f - popProgress).coerceIn(0f, 1f)

                val center = Offset(tile.x + tile.width / 2f, tile.y + tile.height / 2f)

                scale(scale = scaleMultiplier, pivot = center) {
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(
                                theme.topColor.copy(alpha = alpha),
                                theme.bottomColor.copy(alpha = alpha)
                            )
                        ),
                        topLeft = Offset(tile.x, tile.y),
                        size = Size(tile.width, tile.height),
                        cornerRadius = cornerRadius
                    )

                    drawRoundRect(
                        color = theme.highlightColor.copy(alpha = alpha * 0.8f),
                        topLeft = Offset(tile.x, tile.y),
                        size = Size(tile.width, tile.height),
                        cornerRadius = cornerRadius,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            } else {
                // Regular falling tile
                // Tile body
                drawRoundRect(
                    brush = theme.brush,
                    topLeft = Offset(tile.x, tile.y),
                    size = Size(tile.width, tile.height),
                    cornerRadius = cornerRadius
                )

                // Outer border accent
                drawRoundRect(
                    color = theme.highlightColor.copy(alpha = 0.45f),
                    topLeft = Offset(tile.x, tile.y),
                    size = Size(tile.width, tile.height),
                    cornerRadius = cornerRadius,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Top specular highlight line
                val highlightInset = 8.dp.toPx()
                drawLine(
                    color = Color.White.copy(alpha = 0.45f),
                    start = Offset(tile.x + highlightInset, tile.y + 4.dp.toPx()),
                    end = Offset(tile.x + tile.width - highlightInset, tile.y + 4.dp.toPx()),
                    strokeWidth = 2.dp.toPx()
                )

                // Bottom subtle drop shadow border inside
                drawLine(
                    color = theme.shadowColor.copy(alpha = 0.5f),
                    start = Offset(tile.x + highlightInset, tile.y + tile.height - 3.dp.toPx()),
                    end = Offset(tile.x + tile.width - highlightInset, tile.y + tile.height - 3.dp.toPx()),
                    strokeWidth = 2.5.dp.toPx()
                )
            }
        }

        // 5. Draw pop particles
        for (particle in state.particles) {
            val theme = TileGradients[particle.colorIndex % TileGradients.size]
            drawCircle(
                color = theme.topColor.copy(alpha = particle.alpha),
                radius = particle.radius,
                center = Offset(particle.x, particle.y)
            )
        }

        // 6. Draw red danger flash if life was just lost
        if (state.dangerFlashAlpha > 0.01f) {
            drawRect(
                color = CrimsonDanger.copy(alpha = state.dangerFlashAlpha),
                topLeft = Offset.Zero,
                size = size
            )
        }
    }
}
