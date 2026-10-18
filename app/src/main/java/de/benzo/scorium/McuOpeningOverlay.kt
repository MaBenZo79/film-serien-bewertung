package de.benzo.scorium

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

/**
 * Simple MCU intro: fade the current screen into a fullscreen video.
 * The parent displays the phase hub when playback completes.
 * No moving card, scaling, iris, or poster matching.
 */
@Composable
fun McuOpeningOverlay(
    tileBounds: Rect?,
    posterBounds: Rect?,
    imageRevision: Int,
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    var firstFrame by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    var fadeIn by remember { mutableStateOf(false) }
    var fadeOut by remember { mutableStateOf(false) }
    val videoAlpha by animateFloatAsState(
        targetValue = if (fadeOut) 0f else if (fadeIn) 1f else 0f,
        animationSpec = tween(durationMillis = 450),
        label = "mcuIntroVideoFade"
    )

    BackHandler { onSkip() }

    LaunchedEffect(firstFrame) {
        if (firstFrame) fadeIn = true
    }
    LaunchedEffect(finished) {
        if (finished) {
            fadeOut = true
            delay(450)
            onComplete()
        }
    }
    // A fallback for devices where the decoder never produces a frame.
    LaunchedEffect(Unit) {
        delay(12000)
        if (!finished) onComplete()
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.Transparent)
            .graphicsLayer { alpha = videoAlpha }
            .clickable { onSkip() }
    ) {
        AndroidView(
            factory = { ctx ->
                McuCoverVideoView(
                    ctx,
                    onFinished = { finished = true },
                    onStarted = {},
                    onFirstFrame = { firstFrame = true }
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
