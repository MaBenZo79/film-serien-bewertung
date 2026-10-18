package de.benzo.scorium

import android.content.Context
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import android.view.TextureView
import android.widget.FrameLayout

/**
 * A center-cropped video layer. Unlike VideoView, the video fills the animated
 * 4:3-to-fullscreen viewport without letterboxing or stretching its pixels.
 */
class McuCoverVideoView(
    context: Context,
    private val onFinished: () -> Unit
) : FrameLayout(context), TextureView.SurfaceTextureListener {
    private val texture = TextureView(context)
    private var player: MediaPlayer? = null
    // Keep a live Surface until the player has stopped and released it.
    private var playbackSurface: Surface? = null
    private var videoWidth = 720
    private var videoHeight = 1280

    init {
        addView(texture, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        texture.surfaceTextureListener = this
        texture.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> updateCrop() }
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        val media = MediaPlayer()
        player = media
        try {
            media.setDataSource(context, Uri.parse("android.resource://${context.packageName}/${R.raw.mcu_collection_intro}"))
            val renderSurface = Surface(surfaceTexture)
            playbackSurface = renderSurface
            media.setSurface(renderSurface)
            media.isLooping = false
            media.setOnVideoSizeChangedListener { _, w, h ->
                if (w > 0 && h > 0) {
                    videoWidth = w
                    videoHeight = h
                    updateCrop()
                }
            }
            media.setOnPreparedListener {
                it.start()
            }
            media.setOnErrorListener { _, _, _ ->
                releasePlayer()
                onFinished()
                true
            }
            media.setOnCompletionListener { onFinished() }
            media.prepareAsync()
        } catch (_: Exception) {
            releasePlayer()
            onFinished()
        }
    }

    private fun updateCrop() {
        val width = texture.width.toFloat()
        val height = texture.height.toFloat()
        if (width <= 0f || height <= 0f) return
        val sourceRatio = videoWidth.toFloat() / videoHeight
        val viewRatio = width / height
        // TextureView transforms scale video pixels, not the view bounds.
        // Fill the viewport while preserving the original aspect ratio.
        val sx = if (viewRatio > sourceRatio) 1f else sourceRatio / viewRatio
        val sy = if (viewRatio > sourceRatio) viewRatio / sourceRatio else 1f
        texture.setTransform(Matrix().apply { setScale(sx, sy, width / 2f, height / 2f) })
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        updateCrop()
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        releasePlayer()
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) = Unit

    private fun releasePlayer() {
        player?.runCatching { stop() }
        player?.release()
        player = null
        playbackSurface?.release()
        playbackSurface = null
    }

    override fun onDetachedFromWindow() {
        releasePlayer()
        super.onDetachedFromWindow()
    }
}
