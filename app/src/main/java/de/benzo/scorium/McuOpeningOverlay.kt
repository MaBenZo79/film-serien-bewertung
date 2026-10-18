package de.benzo.scorium

import android.net.Uri
import android.widget.VideoView
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

/** Collection-to-screen expansion. Video resource is optional until MCU artwork is bundled. */
@Composable
fun McuOpeningOverlay(onComplete: () -> Unit, onSkip: () -> Unit) {
    val context = LocalContext.current
    val progress = remember { Animatable(0f) }
    val videoId = remember(context) {
        context.resources.getIdentifier("mcu_collection_intro", "raw", context.packageName)
    }
    LaunchedEffect(Unit) {
        progress.animateTo(0.12f, tween(240))
        // Keep the video visible inside the card before expanding it.
        delay(1700)
        progress.animateTo(1f, tween(850))
        delay(180)
        onComplete()
    }
    val p = progress.value
    val corner = (18f * (1f - p)).dp
    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.88f))
            .clickable { onSkip() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .fillMaxWidth(0.76f + 0.24f * p)
                .fillMaxHeight(0.28f + 0.72f * p)
                .clip(RoundedCornerShape(corner))
                .background(Color(0xFF210B17)),
            contentAlignment = Alignment.Center
        ) {
            if (videoId != 0) {
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            setVideoURI(Uri.parse("android.resource://${ctx.packageName}/$videoId"))
                            setOnPreparedListener { player ->
                                player.isLooping = false
                                start()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    "MARVEL CINEMATIC UNIVERSE",
                    color = Color(0xFFF2E7E7),
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }
            Box(
                Modifier.fillMaxSize()
                    .background(Color(0xFFDD233A).copy(alpha = (p * 0.13f).coerceIn(0f, 0.13f)))
            )
        }
        Text(
            "Überspringen",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.BottomEnd).padding(22.dp)
        )
    }
}
