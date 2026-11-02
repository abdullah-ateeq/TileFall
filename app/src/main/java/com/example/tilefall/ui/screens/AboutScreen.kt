package com.example.tilefall.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tilefall.ui.theme.CyanAccent
import com.example.tilefall.ui.theme.TileGradients

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "ABOUT",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // App Emblem
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            val heights = listOf(56.dp, 72.dp, 48.dp, 64.dp)
            TileGradients.take(4).forEachIndexed { index, gradient ->
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = heights[index])
                        .clip(RoundedCornerShape(10.dp))
                        .background(gradient.brush)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "TileFall",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.labelMedium.copy(
                color = CyanAccent,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Description Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "ABOUT THE GAME",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "TileFall is a modern offline hyper-casual arcade game designed with smooth 60fps canvas animations, responsive haptics, and instant touch accuracy.\n\nTap descending tiles before they cross the danger line. Chain consecutive taps together to build multipliers and reach new high scores across Easy, Medium, and Hard difficulties.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Credits Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "CREDITS & TECH",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                CreditItem(role = "Engine & Architecture", name = "Jetpack Compose & Kotlin Coroutines")
                CreditItem(role = "Audio & Haptics", name = "Low-Latency Synthetic PCM AudioTrack")
                CreditItem(role = "Persistence", name = "AndroidX DataStore Preferences")
                CreditItem(role = "Design Language", name = "Modern Material 3 Arcade System")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun CreditItem(role: String, name: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = role,
            style = MaterialTheme.typography.labelSmall.copy(
                color = CyanAccent,
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
