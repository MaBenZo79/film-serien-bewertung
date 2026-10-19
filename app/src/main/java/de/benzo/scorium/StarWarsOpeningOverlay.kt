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
 * Simple DC intro: fade the current screen into a fullscreen video.
 * The parent displays the phase hub when playback completes.
 * No moving card, scaling, iris, or poster matching.
 */
@Composable
fun StarWarsOpeningOverlay(
    tileBounds: Rect?,
    posterBounds: Rect?,
    imageRevision: Int,
    onComplete: () -> Unit,
    onReveal: () -> Unit,
    onSkip: () -> Unit
) {
    var firstFrame by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    var fadeIn by remember { mutableStateOf(false) }
    var fadeOut by remember { mutableStateOf(false) }
    val videoAlpha by animateFloatAsState(
        targetValue = if (fadeOut) 0f else if (fadeIn) 1f else 0f,
        animationSpec = tween(durationMillis = 450),
        label = "dcIntroVideoFade"
    )

    BackHandler { onSkip() }

    LaunchedEffect(firstFrame) {
        if (firstFrame) fadeIn = true
    }
    LaunchedEffect(finished) {
        if (finished) {
            // Put the red phase hub behind the video BEFORE fading it out.
            onReveal()
            fadeOut = true
            delay(450)
            onComplete()
        }
    }
    // A fallback for devices where the decoder never produces a frame.
    LaunchedEffect(Unit) {
        delay(12000)
        if (!finished) { onReveal(); onComplete() }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.Transparent)
            .graphicsLayer { alpha = videoAlpha }
            .clickable { onSkip() }
    ) {
        AndroidView(
            factory = { ctx ->
                DcCoverVideoView(
                    ctx,
                    onFinished = { finished = true },
                    onStarted = {},
                    onFirstFrame = { firstFrame = true },
                    videoResource = R.raw.star_wars_collection_intro
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
