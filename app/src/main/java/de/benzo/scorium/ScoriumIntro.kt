package de.benzo.scorium

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * First screen on each app launch. Uses real artwork separated at build time:
 * slow moving purple clouds, left-to-right light reveal, a single popcorn
 * kernel traveling from the U cup to the I, then a delayed menu button.
 * This is a UI animation, not a pre-rendered video.
 */
@Composable
fun ScoriumIntroScreen(onEnter: () -> Unit) {
 val clouds = remember { Animatable(0f) }
 val letters = remember { Animatable(0f) }
 val kernel = remember { Animatable(0f) }
 val menu = remember { Animatable(0f) }
 val exit = remember { Animatable(0f) }
 var leaving by remember { mutableStateOf(false) }
 val scope = rememberCoroutineScope()

 LaunchedEffect(Unit) {
  launch { clouds.animateTo(1f, tween(2700, easing = LinearOutSlowInEasing)) }
  delay(550)
  letters.animateTo(1f, tween(2350, easing = LinearEasing))
  delay(220)
  kernel.animateTo(1f, tween(950, easing = FastOutSlowInEasing))
  delay(110)
  menu.animateTo(1f, tween(620, easing = LinearOutSlowInEasing))
 }

 // Independent, gentle movement continues once the user can enter the app.
 val nebula = rememberInfiniteTransition(label = "SCORIUM clouds")
 val driftX by nebula.animateFloat(
  initialValue = -24f,
  targetValue = 24f,
  animationSpec = infiniteRepeatable(tween(14500, easing = LinearEasing), RepeatMode.Reverse),
  label = "Horizontal smoke"
 )
 val driftY by nebula.animateFloat(
  initialValue = 18f,
  targetValue = -18f,
  animationSpec = infiniteRepeatable(tween(11700, easing = LinearEasing), RepeatMode.Reverse),
  label = "Vertical smoke"
 )

 Box(
  modifier = Modifier.fillMaxSize().background(Color(0xFF010007))
   .graphicsLayer {
    alpha = 1f - exit.value
    scaleX = 1f + exit.value * .035f
    scaleY = 1f + exit.value * .035f
   }
 ) {
  // Portrait artwork without the baked-in text.
  Image(
   painter = painterResource(R.drawable.scorium_intro_background),
   contentDescription = null,
   contentScale = ContentScale.Crop,
   modifier = Modifier.fillMaxSize().graphicsLayer { alpha = clouds.value }
  )
  // Two transparent fog layers drift past one another instead of merely fading.
  Image(
   painter = painterResource(R.drawable.scorium_intro_fog),
   contentDescription = null,
   contentScale = ContentScale.Crop,
   modifier = Modifier.fillMaxSize().graphicsLayer {
    alpha = .52f * clouds.value
    translationX = driftX
    translationY = driftY
    scaleX = 1.08f
    scaleY = 1.09f
   }
  )
  Image(
   painter = painterResource(R.drawable.scorium_intro_fog),
   contentDescription = null,
   contentScale = ContentScale.Crop,
   modifier = Modifier.fillMaxSize().graphicsLayer {
    alpha = .28f * clouds.value
    translationX = -driftX * .66f
    translationY = -driftY * .8f
    scaleX = -1.06f
    scaleY = 1.06f
   }
  )

  Column(
   modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(horizontal = 7.dp),
   horizontalAlignment = Alignment.CenterHorizontally,
   verticalArrangement = Arrangement.Center
  ) {
   BoxWithConstraints(
    modifier = Modifier.fillMaxWidth().aspectRatio(2137f / 736f)
   ) {
    // The letter I has no popcorn baked into this intro-only image.
    Image(
     painter = painterResource(R.drawable.scorium_intro_wordmark),
     contentDescription = "SCORIUM",
     contentScale = ContentScale.FillBounds,
     modifier = Modifier.fillMaxSize()
      .graphicsLayer { alpha = (letters.value * 3f).coerceIn(0f, 1f) }
      .drawWithContent {
       val progress = letters.value
       val revealed = size.width * (progress * 1.18f).coerceIn(0f, 1f)
       clipRect(left = 0f, top = 0f, right = revealed, bottom = size.height) {
        drawContent()
       }
       // A brilliant, soft vertical light sweep runs ahead of the reveal
       // and exits completely to the right at the end.
       if(progress > .005f && progress < .999f) {
        val x = size.width * (-.07f + progress * 1.20f)
        val radius = size.width * .095f
        drawRect(
         brush = Brush.horizontalGradient(
          colors = listOf(
           Color.Transparent,
           Color(0x554B0DAA),
           Color(0x99F4DFFF),
           Color(0x99FFFFFF),
           Color(0x554B0DAA),
           Color.Transparent
          ),
          startX = x - radius,
          endX = x + radius
         ),
         topLeft = Offset(x - radius, 0f),
         size = Size(radius * 2f, size.height)
        )
       }
      }
    )

    val t = kernel.value
    // The kernel starts at the top of the U's popcorn cup and arcs left
    // onto the I. It stays in place after landing, with a slight rotation.
    if(letters.value >= .999f && (t > 0f || menu.value > 0f)) {
     val startX = maxWidth * .735f
     val targetX = maxWidth * .568f
     val x = startX + (targetX - startX) * t
     val baseY = maxHeight * (.25f - .145f * t)
     val arcY = maxHeight * (.37f * sin(PI * t).toFloat())
     Image(
      painter = painterResource(R.drawable.scorium_intro_kernel),
      contentDescription = null,
      contentScale = ContentScale.FillBounds,
      modifier = Modifier
       .offset(x = x, y = baseY - arcY)
       .width(maxWidth * .055f)
       .aspectRatio(116f / 104f)
       .graphicsLayer {
        rotationZ = (1f - t) * 34f
        scaleX = .86f + t * .14f
        scaleY = .86f + t * .14f
       }
     )
    }
   }

   Spacer(Modifier.height(38.dp))
   Button(
    onClick = {
     if(!leaving) {
      leaving = true
      scope.launch {
       exit.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
       onEnter()
      }
     }
    },
    enabled = menu.value >= .99f && !leaving,
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.buttonColors(
     containerColor = Color(0xFF56188E),
     contentColor = Color.White
    ),
    modifier = Modifier.width(220.dp).height(54.dp)
     .alpha(menu.value)
     .offset(y = (16f * (1f - menu.value)).dp)
     .border(1.dp, Color(0xFFAD64E9), RoundedCornerShape(16.dp))
   ) {
    Text("Zum Hauptmenü", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
   }
  }
 }
}
