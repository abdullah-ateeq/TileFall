package com.example.tilefall.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tilefall.model.Difficulty
import com.example.tilefall.ui.components.ArcadeButton
import com.example.tilefall.ui.components.ArcadeOutlineButton
import com.example.tilefall.ui.components.StatCard
import com.example.tilefall.ui.theme.AmberAccent
import com.example.tilefall.ui.theme.CrimsonDanger
import com.example.tilefall.ui.theme.CyanAccent
import com.example.tilefall.ui.theme.EmeraldAccent

@Composable
fun ResultScreen(
    score: Int,
    bestScore: Int,
    tilesTapped: Int,
    tilesMissed: Int,
    highestStreak: Int,
    difficulty: Difficulty,
    onPlayAgain: () -> Unit,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isNewRecord = score > 0 && score >= bestScore
    val scrollState = rememberScrollState()

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
            Spacer(modifier = Modifier.height(44.dp))

            // Game Over header
            Text(
                text = "GAME OVER",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = CrimsonDanger
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${difficulty.title.uppercase()} MODE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Score Highlight Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isNewRecord) AmberAccent.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isNewRecord) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AmberAccent.copy(alpha = 0.16f))
                                .border(1.dp, AmberAccent, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AmberAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "NEW HIGH SCORE!",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AmberAccent,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    Text(
                        text = "FINAL SCORE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = score.toString(),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = if (isNewRecord) AmberAccent else CyanAccent
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Best: $bestScore",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Detailed Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Tapped",
                    value = tilesTapped.toString(),
                    modifier = Modifier.weight(1f),
                    accentColor = EmeraldAccent
                )

                StatCard(
                    title = "Missed",
                    value = tilesMissed.toString(),
                    modifier = Modifier.weight(1f),
                    accentColor = CrimsonDanger
                )

                StatCard(
                    title = "Max Streak",
                    value = "${highestStreak}x",
                    modifier = Modifier.weight(1f),
                    accentColor = AmberAccent
                )
            }
        }

        // Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ArcadeButton(
                text = "PLAY AGAIN",
                icon = Icons.Default.Refresh,
                onClick = onPlayAgain
            )

            ArcadeOutlineButton(
                text = "MAIN MENU",
                icon = Icons.Default.Home,
                onClick = onNavigateHome
            )
        }
    }
}
