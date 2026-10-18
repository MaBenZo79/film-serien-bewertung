package de.benzo.scorium

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.filterNotNull
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Stage 1: the tapped 4:3 collection card moves from its real window coordinates
 * to the center. Stage 2: the centered card grows to the full available viewport.
 * The portrait intro and MCU destination screen are added in subsequent steps.
 */
@Composable
fun McuOpeningOverlay(
    tileBounds: Rect?,
    posterBounds: Rect?,
    imageRevision: Int,
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    val density = LocalDensity.current
    val move = remember { Animatable(0f) }
    val enlarge = remember { Animatable(0f) }
    var overlayBounds by remember { mutableStateOf<Rect?>(null) }
    var playVideo by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    var firstVideoFrame by remember { mutableStateOf(false) }
    var handoff by remember { mutableStateOf(false) }
    val posterOpacity by animateFloatAsState(
        targetValue = if (handoff) 0f else 1f,
        animationSpec = tween(durationMillis = 520), label = "mcuPosterHandoff"
    )

    BackHandler { onSkip() }

    LaunchedEffect(Unit) {
        // A single playback sequence: layout updates must not restart the animation.
        snapshotFlow { overlayBounds }.filterNotNull().first()
        move.snapTo(0f)
        enlarge.snapTo(0f)
        // Prepare the fullscreen video during the single continuous tile movement.
        // Keep it hidden until its first decoded frame is available.
        playVideo = true
        val travel = launch {
            move.animateTo(1f, tween(durationMillis = 1000, easing = FastOutSlowInEasing))
        }
        val grow = launch {
            enlarge.animateTo(1f, tween(durationMillis = 1000, easing = FastOutSlowInEasing))
        }
        travel.join()
        grow.join()
        snapshotFlow { firstVideoFrame }.first { it }
        handoff = true
    }
    LaunchedEffect(finished) {
        if (finished) onComplete()
    }
    // Fail-safe if media playback cannot start on a particular device.
    LaunchedEffect(playVideo) {
        if (playVideo) {
            delay(10000)
            if (!finished) onComplete()
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
            .onGloballyPositioned { overlayBounds = it.boundsInWindow() }
            .background(Color.Transparent)
            .clickable { onSkip() }
    ) {
        val bounds = overlayBounds
        if (bounds != null) {
            val fullWidth = constraints.maxWidth.toFloat()
            val fullHeight = constraints.maxHeight.toFloat()
            val original = tileBounds ?: Rect(
                left = fullWidth * 0.25f + bounds.left,
                top = fullHeight * 0.35f + bounds.top,
                right = fullWidth * 0.75f + bounds.left,
                bottom = fullHeight * 0.65f + bounds.top
            )
            val cardWidth = original.width.coerceAtLeast(1f)
            val cardHeight = original.height.coerceAtLeast(1f)
            val startX = original.left - bounds.left
            val startY = original.top - bounds.top
            val centerX = (fullWidth - cardWidth) / 2f
            val centerY = (fullHeight - cardHeight) / 2f
            // Keep the same center while expanding; the visible area grows from 4:3.
            val movedX = startX + (centerX - startX) * move.value
            val movedY = startY + (centerY - startY) * move.value
            // The collection card moves unchanged. Only the actual poster is the reveal origin.
            val poster = posterBounds ?: original
            // Enlarge the tile so its poster approaches the width of the fullscreen
            // reference image. The fullscreen video itself never scales.
            val basePosterW = poster.width.coerceAtLeast(1f)
            val basePosterH = poster.height.coerceAtLeast(1f)
            val targetScale = (fullWidth / basePosterW).coerceAtLeast(1f)
            val cardScale = 1f + (targetScale - 1f) * enlarge.value
            val posterW = basePosterW * cardScale
            val posterH = basePosterH * cardScale
            val tileCenterX = movedX + cardWidth / 2f
            val tileCenterY = movedY + cardHeight / 2f
            val posterCenterOffsetX = poster.center.x - original.center.x
            val posterCenterOffsetY = poster.center.y - original.center.y
            // The new portrait intro stays at its final fullscreen dimensions.
            // No iris or circular clipping: fade the video in over the centered,
            // enlarged tile, while the tile and its glowing border dissolve.
            if (playVideo) {
                AndroidView(
                    factory = { ctx ->
                        McuCoverVideoView(ctx,
                            onFinished = { finished = true },
                            onStarted = {},
                            onFirstFrame = { firstVideoFrame = true })
                    },
                    modifier = Modifier.fillMaxSize().graphicsLayer {
                        alpha = if (firstVideoFrame) 1f - posterOpacity else 0f
                    }
                )
            }
            // Keep the full card static while fading it; never scale its text/buttons.
            if (posterOpacity > 0f) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(movedX.roundToInt(), movedY.roundToInt()) }
                        .width(with(density) { cardWidth.toDp() })
                        .height(with(density) { cardHeight.toDp() })
                        .graphicsLayer {
                            alpha = posterOpacity
                            scaleX = cardScale
                            scaleY = cardScale
                        },
                    contentAlignment = Alignment.Center
                ) {
                    ScoriumCollectionTile(
                        "Marvel / MCU", "Filme & Serien", "collection::mcu",
                        onClick = {}, onImageSaved = {}, revision = imageRevision
                    )
                }
            }
        }
        Text(
            "Tippen zum Überspringen",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.BottomEnd).padding(22.dp)
        )
    }
}
