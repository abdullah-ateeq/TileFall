package com.example.tilefall.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tilefall.ui.theme.CyanAccent
import com.example.tilefall.ui.theme.TileGradients
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }
    val tileOffsetY = remember { Animatable(-100f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
        tileOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        delay(900L)
        onNavigateToHome()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onNavigateToHome() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            // Animated brand tile emblem
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.offset { IntOffset(0, tileOffsetY.value.toInt()) }
            ) {
                val heights = listOf(70.dp, 90.dp, 60.dp, 80.dp)
                TileGradients.take(4).forEachIndexed { index, gradient ->
                    Box(
                        modifier = Modifier
                            .size(width = 42.dp, height = heights[index])
                            .clip(RoundedCornerShape(12.dp))
                            .background(gradient.brush)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "TILEFALL",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "PRECISION ARCADE",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )
            )
        }
    }
}
