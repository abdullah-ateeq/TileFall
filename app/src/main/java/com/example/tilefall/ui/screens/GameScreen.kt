package com.example.tilefall.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tilefall.data.AppThemeMode
import com.example.tilefall.model.Difficulty
import com.example.tilefall.model.GameStatus
import com.example.tilefall.ui.components.ArcadeButton
import com.example.tilefall.ui.components.ArcadeOutlineButton
import com.example.tilefall.ui.components.LivesIndicator
import com.example.tilefall.ui.components.PlayfieldCanvas
import com.example.tilefall.ui.components.StreakBadge
import com.example.tilefall.ui.theme.CyanAccent
import com.example.tilefall.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    themeMode: AppThemeMode,
    difficulty: Difficulty,
    onGameOver: (score: Int, bestScore: Int, tapped: Int, missed: Int, streak: Int, diff: Difficulty) -> Unit,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.gameState.collectAsState()
    val density = LocalDensity.current.density
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    // Start game on initial composition or difficulty change
    LaunchedEffect(Unit) {
        viewModel.startGame(difficulty)
    }

    // React to Game Over
    LaunchedEffect(state.status) {
        if (state.status == GameStatus.GAME_OVER) {
            onGameOver(
                state.score,
                state.bestScore,
                state.tilesTapped,
                state.tilesMissed,
                state.highestStreak,
                state.difficulty
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Shaken Playfield wrapper
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        state.shakeOffsetX.toInt(),
                        state.shakeOffsetY.toInt()
                    )
                }
                .onSizeChanged { size ->
                    if (size.width > 0 && size.height > 0) {
                        viewModel.onPlayfieldSizeChanged(
                            width = size.width.toFloat(),
                            height = size.height.toFloat(),
                            screenDensity = density
                        )
                    }
                }
        ) {
            PlayfieldCanvas(
                state = state,
                isDarkTheme = isDark,
                onTap = { x, y -> viewModel.handleTap(x, y) }
            )
        }

        // Top HUD Overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score & Streak
                Column {
                    Text(
                        text = state.score.toString(),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    StreakBadge(
                        streak = state.streak,
                        multiplier = state.streakMultiplier
                    )
                }

                // Lives and Pause action
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LivesIndicator(
                        lives = state.lives,
                        maxLives = state.maxLives
                    )

                    IconButton(
                        onClick = { viewModel.pauseGame() },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause Game",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Pause Menu Dialog Overlay
        AnimatedVisibility(
            visible = state.status == GameStatus.PAUSED,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.72f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "PAUSED",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Current Score: ${state.score}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        ArcadeButton(
                            text = "RESUME",
                            icon = Icons.Default.PlayArrow,
                            onClick = { viewModel.resumeGame() }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ArcadeOutlineButton(
                            text = "RESTART",
                            icon = Icons.Default.Refresh,
                            onClick = { viewModel.restartGame() }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ArcadeOutlineButton(
                            text = "HOME",
                            icon = Icons.Default.Home,
                            onClick = {
                                viewModel.pauseGame()
                                onNavigateHome()
                            }
                        )
                    }
                }
            }
        }
    }
}
