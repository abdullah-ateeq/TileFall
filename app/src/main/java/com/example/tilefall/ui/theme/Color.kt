package com.example.tilefall.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Dark Palette Foundation
val Slate950 = Color(0xFF090D16)
val Slate900 = Color(0xFF0F172A)
val Slate850 = Color(0xFF141F36)
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Slate600 = Color(0xFF475569)
val Slate400 = Color(0xFF94A3B8)
val Slate300 = Color(0xFFCBD5E1)
val Slate100 = Color(0xFFF1F5F9)

// Light Palette Foundation
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEDF2F7)
val LightOutline = Color(0xFFE2E8F0)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)

// Accents
val CyanAccent = Color(0xFF38BDF8)
val BlueAccent = Color(0xFF3B82F6)
val PurpleAccent = Color(0xFFA855F7)
val AmberAccent = Color(0xFFF59E0B)
val EmeraldAccent = Color(0xFF10B981)
val CrimsonDanger = Color(0xFFFF3366)
val DangerGlow = Color(0x66FF3366)

// Tile Gradient Palettes
data class TileColorTheme(
    val topColor: Color,
    val bottomColor: Color,
    val highlightColor: Color,
    val shadowColor: Color
) {
    val brush: Brush
        get() = Brush.verticalGradient(listOf(topColor, bottomColor))
}

val TileGradients = listOf(
    // 0: Electric Cyan
    TileColorTheme(
        topColor = Color(0xFF38BDF8),
        bottomColor = Color(0xFF0284C7),
        highlightColor = Color(0xFFBAE6FD),
        shadowColor = Color(0xFF0369A1)
    ),
    // 1: Vivid Purple
    TileColorTheme(
        topColor = Color(0xFFC084FC),
        bottomColor = Color(0xFF7E22CE),
        highlightColor = Color(0xFFF3E8FF),
        shadowColor = Color(0xFF6B21A8)
    ),
    // 2: Radiant Emerald
    TileColorTheme(
        topColor = Color(0xFF34D399),
        bottomColor = Color(0xFF059669),
        highlightColor = Color(0xFFD1FAE5),
        shadowColor = Color(0xFF047857)
    ),
    // 3: Warm Amber
    TileColorTheme(
        topColor = Color(0xFFFBBF24),
        bottomColor = Color(0xFFD97706),
        highlightColor = Color(0xFFFEF3C7),
        shadowColor = Color(0xFFB45309)
    ),
    // 4: Coral Rose
    TileColorTheme(
        topColor = Color(0xFFFB7185),
        bottomColor = Color(0xFFE11D48),
        highlightColor = Color(0xFFFFE4E6),
        shadowColor = Color(0xFFBE123C)
    ),
    // 5: Sapphire Blue
    TileColorTheme(
        topColor = Color(0xFF60A5FA),
        bottomColor = Color(0xFF2563EB),
        highlightColor = Color(0xFFDBEAFE),
        shadowColor = Color(0xFF1D4ED8)
    )
)
