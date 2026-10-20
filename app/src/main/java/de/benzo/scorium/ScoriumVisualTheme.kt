package de.benzo.scorium

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * SCORIUM design system v1.
 * One consistent dark, cinematic hierarchy across the app. Collection colours
 * are accents, never an excuse to switch to bright full-screen surfaces.
 * This file has no storage, navigation or rating logic.
 */
data class ScoriumVisualPalette(
    val accent: Color,
    val accentSoft: Color,
    val backdrop: Color,
    val surface: Color,
    val elevated: Color,
    val inset: Color,
    val border: Color,
    val mutedText: Color
)

private val neutralVisuals = ScoriumVisualPalette(
    accent = Color(0xFFBDA4FF),
    accentSoft = Color(0xFF8062C6),
    backdrop = Color(0xFF05040A),
    surface = Color(0xFF13121F),
    elevated = Color(0xFF1C192C),
    inset = Color(0xFF0C0B16),
    border = Color(0xFF393149),
    mutedText = Color(0xFFC0B9CE)
)
private val marvelVisuals = ScoriumVisualPalette(
    accent = Color(0xFFF05B66),
    accentSoft = Color(0xFFAD3744),
    backdrop = Color(0xFF0E0408),
    surface = Color(0xFF201015),
    elevated = Color(0xFF31161E),
    inset = Color(0xFF10070C),
    border = Color(0xFF613039),
    mutedText = Color(0xFFE2BDC3)
)
private val dcVisuals = ScoriumVisualPalette(
    accent = Color(0xFF78B4FF),
    accentSoft = Color(0xFF3977BC),
    backdrop = Color(0xFF060A14),
    surface = Color(0xFF101B2B),
    elevated = Color(0xFF182A42),
    inset = Color(0xFF080F1D),
    border = Color(0xFF345477),
    mutedText = Color(0xFFC4D5E8)
)
private val starWarsVisuals = ScoriumVisualPalette(
    accent = Color(0xFFE8C87B),
    accentSoft = Color(0xFFAA873E),
    backdrop = Color(0xFF080809),
    surface = Color(0xFF18171A),
    elevated = Color(0xFF25221E),
    inset = Color(0xFF0E0D0E),
    border = Color(0xFF504535),
    mutedText = Color(0xFFDCD1BB)
)

fun scoriumVisualsFor(mcu: Boolean, dc: Boolean, starWars: Boolean): ScoriumVisualPalette =
    when {
        starWars -> starWarsVisuals
        dc -> dcVisuals
        mcu -> marvelVisuals
        else -> neutralVisuals
    }

@Composable
fun scoriumVisuals(): ScoriumVisualPalette = scoriumVisualsFor(
    mcu = LocalMcuCollectionTheme.current,
    dc = LocalDcCollectionTheme.current,
    starWars = LocalStarWarsCollectionTheme.current
)

fun scoriumColorScheme(mcu: Boolean, dc: Boolean, starWars: Boolean): ColorScheme {
    val v = scoriumVisualsFor(mcu, dc, starWars)
    return darkColorScheme(
        primary = v.accent,
        onPrimary = v.backdrop,
        primaryContainer = v.elevated,
        onPrimaryContainer = Color.White,
        secondary = v.accentSoft,
        onSecondary = Color.White,
        background = v.backdrop,
        onBackground = Color.White,
        surface = v.surface,
        onSurface = Color.White,
        surfaceVariant = v.elevated,
        onSurfaceVariant = v.mutedText,
        outline = v.border,
        outlineVariant = v.border.copy(alpha = 0.6f),
        surfaceTint = v.accent
    )
}

val ScoriumShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)
