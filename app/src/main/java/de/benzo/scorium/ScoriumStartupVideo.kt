package de.benzo.scorium

import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import android.view.TextureView
import android.graphics.SurfaceTexture
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

/** Plays the supplied SCORIUM MP4 and dissolves into the already composed home screen. */
@Composable
fun ScoriumStartupVideo(resourceId: Int, onEnter: () -> Unit) {
 val context = LocalContext.current
 val fade = remember { Animatable(1f) }
 val finished = remember { androidx.compose.runtime.mutableStateOf(false) }
 LaunchedEffect(finished.value) {
  if(finished.value) {
   fade.animateTo(0f, tween(650))
   onEnter()
  }
 }
 Box(Modifier.fillMaxSize().background(Color.Black).graphicsLayer { alpha = fade.value }) {
  AndroidView(factory = { ctx ->
   TextureView(ctx).apply {
    surfaceTextureListener = object : TextureView.SurfaceTextureListener {
     var player: MediaPlayer? = null
     var surface: Surface? = null
     override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
      try {
       val output = Surface(texture)
       surface = output
       player = MediaPlayer().apply {
        setDataSource(ctx, Uri.parse("android.resource://${ctx.packageName}/$resourceId"))
        setSurface(output)
        setOnPreparedListener { it.start() }
        setOnCompletionListener { finished.value = true }
        setOnErrorListener { _, _, _ -> finished.value = true; true }
        prepareAsync()
       }
      } catch (_: Exception) { finished.value = true }
     }
     override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {}
     override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
     override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
      player?.runCatching { stop() }
      player?.release()
      player = null
      surface?.release()
      surface = null
      return true
     }
    }
   }
  }, modifier = Modifier.fillMaxSize())
 }
}
