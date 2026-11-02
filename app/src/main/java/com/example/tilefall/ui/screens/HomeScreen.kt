package com.example.tilefall.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tilefall.data.UserSettings
import com.example.tilefall.model.Difficulty
import com.example.tilefall.ui.components.ArcadeButton
import com.example.tilefall.ui.components.DifficultySelector
import com.example.tilefall.ui.components.StatCard
import com.example.tilefall.ui.theme.AmberAccent
import com.example.tilefall.ui.theme.CyanAccent
import com.example.tilefall.ui.theme.TileGradients

@Composable
fun HomeScreen(
    settings: UserSettings,
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit,
    onPlayClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onAboutClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Minimal animated falling tiles preview in banner
    val infiniteTransition = rememberInfiniteTransition(label = "homeTiles")
    val fallProgress1 by infiniteTransition.animateFloat(
        initialValue = -80f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tile1"
    )
    val fallProgress2 by infiniteTransition.animateFloat(
        initialValue = -120f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tile2"
    )
    val fallProgress3 by infiniteTransition.animateFloat(
        initialValue = -90f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, delayMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tile3"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // Top action buttons row (Settings & About)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onAboutClicked,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = onSettingsClicked,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Game Title
            Text(
                text = "TILEFALL",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tap tiles before they hit the bottom.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Minimal animated preview box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    // Preview Tile 1
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(50.dp.roundToPx(), fallProgress1.dp.roundToPx()) }
                            .size(width = 64.dp, height = 75.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TileGradients[0].brush)
                    )

                    // Preview Tile 2
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(145.dp.roundToPx(), fallProgress2.dp.roundToPx()) }
                            .size(width = 64.dp, height = 85.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TileGradients[1].brush)
                    )

                    // Preview Tile 3
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(240.dp.roundToPx(), fallProgress3.dp.roundToPx()) }
                            .size(width = 64.dp, height = 70.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TileGradients[2].brush)
                    )

                    // Bottom line
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(CyanAccent.copy(alpha = 0.5f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Best Score & Best Streak cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Best Score",
                    value = settings.bestScore.toString(),
                    modifier = Modifier.weight(1f),
                    accentColor = AmberAccent
                )

                StatCard(
                    title = "Best Streak",
                    value = "${settings.highestStreak}x",
                    modifier = Modifier.weight(1f),
                    accentColor = CyanAccent
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Difficulty Mode selector
            Text(
                text = "DIFFICULTY",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            DifficultySelector(
                selectedDifficulty = selectedDifficulty,
                onDifficultySelected = onDifficultySelected
            )
        }

        // Bottom Play Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ArcadeButton(
                text = "PLAY",
                icon = Icons.Default.PlayArrow,
                onClick = onPlayClicked
            )
        }
    }
}
