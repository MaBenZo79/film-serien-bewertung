package de.benzo.scorium

import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

/** Plays the bundled MCU clip once, expands the collection card, then opens the catalog. */
@Composable
fun McuOpeningOverlay(onComplete: () -> Unit, onSkip: () -> Unit) {
    val context = LocalContext.current
    val progress = remember { Animatable(0f) }
    var videoFinished by remember { mutableStateOf(false) }
    val videoId = R.raw.mcu_collection_intro

    BackHandler { onSkip() }

    LaunchedEffect(Unit) {
        // The clip starts in the collection-card-sized frame.
        delay(1550)
        progress.animateTo(1f, tween(durationMillis = 900))
        // Allow the roughly three-second clip to finish before opening the catalog.
        delay(750)
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.94f))
            .clickable { onSkip() },
        contentAlignment = Alignment.Center
    ) {
        val expansion = progress.value
        Box(
            modifier = Modifier
                .fillMaxWidth(0.76f + 0.24f * expansion)
                .fillMaxHeight(0.28f + 0.72f * expansion)
                .clip(RoundedCornerShape((18f * (1f - expansion)).dp))
                .background(Color(0xFF210B17)),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        setVideoURI(Uri.parse("android.resource://${ctx.packageName}/$videoId"))
                        setOnPreparedListener { player ->
                            player.isLooping = false
                            start()
                        }
                        setOnCompletionListener { videoFinished = true }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            Box(
                Modifier.fillMaxSize().background(
                    Color(0xFFDD233A).copy(alpha = (expansion * 0.13f).coerceIn(0f, 0.13f))
                )
            )
        }
        Text(
            "Tippen zum Überspringen",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.BottomEnd).padding(22.dp)
        )
    }
}
