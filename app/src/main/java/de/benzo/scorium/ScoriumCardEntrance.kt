package de.benzo.scorium

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Shared cinematic card entrance.
 * A compact, slightly bouncy pop paired with a short left-to-right sweep.
 * The short 74 ms stagger deliberately overlaps neighbouring animations
 * like quickly turning successive pages. Content remains in its real layout
 * position throughout; only its render transform changes.
 */
@Composable
fun ScoriumCardEntrance(
    index: Int,
    visible: Boolean = true,
    startDelayMillis: Long = 260,
    skipAnimation: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val progress = remember { Animatable(if(visible && skipAnimation) 1f else 0f) }
    LaunchedEffect(visible, skipAnimation) {
        if (!visible) {
            progress.snapTo(0f)
        } else if (skipAnimation) {
            progress.snapTo(1f)
        } else {
            delay(startDelayMillis + index.coerceAtLeast(0) * 74L)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.76f,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }
    val density = LocalDensity.current
    val shiftX = with(density) { 20.dp.toPx() }
    val shiftY = with(density) { 10.dp.toPx() }
    Box(modifier.graphicsLayer {
        val p = progress.value
        alpha = p.coerceIn(0f, 1f)
        translationX = -(1f - p) * shiftX
        translationY = (1f - p) * shiftY
        scaleX = 0.91f + 0.09f * p
        scaleY = 0.91f + 0.09f * p
    }) {
        content()
    }
}

/**
 * Fade a complete franchise hub, including its background, logo and headings.
 * Individual cards then cascade in independently using ScoriumCardEntrance.
 */
@Composable
fun ScoriumHubPage(content: @Composable () -> Unit) {
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) { alpha.animateTo(1f, tween(durationMillis = 480)) }
    Box(Modifier.graphicsLayer { this.alpha = alpha.value }) {
        content()
    }
}
