package com.example.tilefall.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tilefall.model.Difficulty
import com.example.tilefall.ui.theme.AmberAccent
import com.example.tilefall.ui.theme.CrimsonDanger
import com.example.tilefall.ui.theme.CyanAccent
import com.example.tilefall.ui.theme.EmeraldAccent
import com.example.tilefall.ui.theme.PurpleAccent

@Composable
fun ArcadeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    color: Color = CyanAccent,
    textColor: Color = Color.Black
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

@Composable
fun ArcadeOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, color.copy(alpha = 0.4f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun LivesIndicator(
    lives: Int,
    maxLives: Int = 3,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heartPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (lives == 1) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxLives) {
            val isActive = i <= lives
            val isUrgent = isActive && lives == 1
            Icon(
                imageVector = if (isActive) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Life $i",
                tint = if (isActive) CrimsonDanger else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier
                    .size(24.dp)
                    .scale(if (isUrgent) pulseScale else 1f)
            )
        }
    }
}

@Composable
fun StreakBadge(
    streak: Int,
    multiplier: Float,
    modifier: Modifier = Modifier
) {
    if (streak <= 0) return

    val infiniteTransition = rememberInfiniteTransition(label = "streakGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val badgeColor = when {
        streak >= 15 -> CrimsonDanger
        streak >= 10 -> PurpleAccent
        streak >= 5 -> AmberAccent
        else -> CyanAccent
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(badgeColor.copy(alpha = 0.16f))
            .border(
                BorderStroke(1.5.dp, badgeColor.copy(alpha = glowAlpha)),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "STREAK $streak",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = badgeColor,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            )
            if (multiplier > 1.0f) {
                Text(
                    text = "• ${String.format("%.1f", multiplier)}x",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = badgeColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    accentColor: Color = CyanAccent
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = accentColor
                ),
                textAlign = TextAlign.Center
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DifficultySelector(
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Difficulty.values().forEach { difficulty ->
            val isSelected = difficulty == selectedDifficulty
            val color = when (difficulty) {
                Difficulty.EASY -> EmeraldAccent
                Difficulty.MEDIUM -> CyanAccent
                Difficulty.HARD -> CrimsonDanger
            }

            val animBgColor by animateColorAsState(
                targetValue = if (isSelected) color else Color.Transparent,
                label = "diffBg"
            )
            val animTextColor by animateColorAsState(
                targetValue = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "diffText"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(animBgColor)
                    .clickable { onDifficultySelected(difficulty) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = difficulty.title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = animTextColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                )
            }
        }
    }
}
