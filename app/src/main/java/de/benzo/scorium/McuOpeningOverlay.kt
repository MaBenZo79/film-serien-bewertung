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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Stage 1: the tapped 4:3 collection card moves from its real window coordinates
 * to the center. Stage 2: the centered card grows to the full available viewport.
 * The portrait intro and MCU destination screen are added in subsequent steps.
 */
@Composable
fun McuOpeningOverlay(
    tileBounds: Rect?,
    imageRevision: Int,
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    val density = LocalDensity.current
    val move = remember { Animatable(0f) }
    val expand = remember { Animatable(0f) }
    var overlayBounds by remember { mutableStateOf<Rect?>(null) }
    var playVideo by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    var videoStarted by remember { mutableStateOf(false) }
    var firstVideoFrame by remember { mutableStateOf(false) }
    val posterOpacity by animateFloatAsState(
        targetValue = if (firstVideoFrame) 0f else 1f,
        animationSpec = tween(durationMillis = 220), label = "mcuPosterHandoff"
    )

    BackHandler { onSkip() }

    LaunchedEffect(Unit) {
        // A single playback sequence: layout updates must not restart the animation.
        snapshotFlow { overlayBounds }.filterNotNull().first()
        move.snapTo(0f)
        expand.snapTo(0f)
        move.animateTo(1f, tween(durationMillis = 650, easing = FastOutSlowInEasing))
        delay(160)
        playVideo = true
        // Do not expand an empty video surface: wait until playback really starts.
        snapshotFlow { firstVideoFrame }.first { it }
        // Fade the original card only once a real video frame exists.
        delay(240)
        expand.animateTo(1f, tween(durationMillis = 1050, easing = LinearOutSlowInEasing))
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
            .background(Color(0xFF39070C).copy(alpha = expand.value.coerceIn(0f, 1f)))
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
            val w = cardWidth + (fullWidth - cardWidth) * expand.value
            val h = cardHeight + (fullHeight - cardHeight) * expand.value
            val x = movedX + (cardWidth - w) * 0.5f
            val y = movedY + (cardHeight - h) * 0.5f
            val radius = 16.dp * (1f - expand.value)
            Box(
                modifier = Modifier
                    .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                    .width(with(density) { w.toDp() })
                    .height(with(density) { h.toDp() })
                    .clip(RoundedCornerShape(radius))
                    .background(Color(0xFF620402)),
                contentAlignment = Alignment.Center
            ) {
                if (playVideo) {
                    AndroidView(
                        factory = { ctx ->
                            McuCoverVideoView(ctx, onFinished = { finished = true }, onStarted = { videoStarted = true }, onFirstFrame = { firstVideoFrame = true })
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                if (posterOpacity > 0f) {
                    Box(Modifier.fillMaxSize().graphicsLayer { alpha = posterOpacity }, contentAlignment = Alignment.Center) {
                        ScoriumCollectionTile(
                            "Marvel / MCU", "Filme & Serien", "collection::mcu",
                            onClick = {}, onImageSaved = {}, revision = imageRevision
                        )
                    }
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
