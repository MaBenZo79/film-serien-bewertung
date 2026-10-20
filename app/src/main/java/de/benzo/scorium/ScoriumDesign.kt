package de.benzo.scorium

import androidx.compose.animation.core.*
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInWindow

private val ink=Color(0xFF090C1B)
private val slate=Color(0xFF1A203A)
private val gold=Color(0xFFBDA4FF)
private val fog=Color(0xFFC0B9CE)

val LocalMcuCollectionTheme = compositionLocalOf { false }
val LocalDcCollectionTheme = compositionLocalOf { false }
val LocalStarWarsCollectionTheme = compositionLocalOf { false }
private val dcAccent = Color(0xFF438FEA)
private val mcuAccent = Color(0xFFEF3945)
private val mcuSurface = Color(0xFF30090F)

@Composable
fun ScoriumBackdrop(rootAlpha:Float=1f,content: @Composable BoxScope.()->Unit) {
 val visuals=scoriumVisuals()
 val branded=LocalMcuCollectionTheme.current || LocalDcCollectionTheme.current || LocalStarWarsCollectionTheme.current
 Box(Modifier.fillMaxSize().graphicsLayer { alpha=rootAlpha }.background(visuals.backdrop)) {
  if(!branded) {
   Image(painter=painterResource(R.drawable.scorium_menu_background),
    contentDescription=null,modifier=Modifier.fillMaxSize(),
    contentScale=ContentScale.FillBounds,alpha=0.85f)
  } else {
   Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(
    visuals.backdrop,visuals.elevated.copy(alpha=0.96f),
    visuals.surface,visuals.backdrop))))
  }
  Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(
   Color.Black.copy(alpha=0.11f),Color.Transparent,Color.Black.copy(alpha=0.29f)))))
  content()
 }
}

@Composable
fun ScoriumHeader() {
 Box(Modifier.fillMaxWidth().padding(top=12.dp,bottom=10.dp),contentAlignment=Alignment.Center) {
  Image(painter=painterResource(R.drawable.scorium_header),contentDescription="SCORIUM",modifier=Modifier.width(280.dp).height(66.dp),contentScale=ContentScale.Fit,alignment=Alignment.Center)
 }
}

@Composable
fun ScoriumHero(onExplore:()->Unit) {
 val transition=rememberInfiniteTransition(label="hero")
 val offset by transition.animateFloat(0f,1f,infiniteRepeatable(tween(4200,easing=EaseInOutSine),RepeatMode.Reverse),label="float")
 Card(onClick=onExplore,shape=RoundedCornerShape(26.dp),modifier=Modifier.fillMaxWidth().height(238.dp),
  colors=CardDefaults.cardColors(containerColor=slate)) {
  Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(
   Color(0xFF4D1728),Color(0xFF1D172B),Color(0xFF0F2033))))) {
   Box(Modifier.align(Alignment.CenterEnd).offset(x=48.dp,y=(-18).dp)
    .size(210.dp).graphicsLayer { rotationZ=-13f; translationY=offset*12f }
    .background(Brush.linearGradient(listOf(Color(0xFF8D3544),Color(0xFF3D1726))),
     RoundedCornerShape(28.dp)).border(1.dp,Color(0x55FFFFFF),RoundedCornerShape(28.dp)))
   Text("M",modifier=Modifier.align(Alignment.CenterEnd).offset(x=(-40).dp,y=(-18).dp)
    .graphicsLayer { rotationZ=-13f;translationY=offset*12f },
    fontSize=112.sp,color=Color(0x44FFFFFF),fontWeight=FontWeight.Black)
   Column(Modifier.align(Alignment.BottomStart).padding(23.dp)) {
    Text("FRANCHISE  /  01",fontSize=10.sp,letterSpacing=2.sp,color=gold,fontWeight=FontWeight.Bold)
    Spacer(Modifier.height(11.dp))
    Text("MARVEL",fontSize=38.sp,fontWeight=FontWeight.Black,color=Color.White)
    Text("CINEMATIC UNIVERSE",fontSize=13.sp,letterSpacing=1.sp,color=Color(0xFFF3D9D8))
    Spacer(Modifier.height(14.dp))
    Text("ARCHIV ÖFFNEN  →",fontSize=12.sp,color=gold,fontWeight=FontWeight.Bold)
   }
  }
 }
}

/** Bounded thumbnail cache shared across rows, avoiding full-resolution decoding on the UI thread. */
private val posterThumbnailCache = object : LruCache<String, Bitmap>(24 * 1024) {
 override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
}
private fun loadPosterThumbnail(context:Context,file:java.io.File): Bitmap? {
 // User edits and previously copied originals always take precedence.
 // When no override exists, decode the bundled JPEG directly from APK assets.
 // Only the visible thumbnail is decoded, not the 273 full images at once.
 val local = file.isFile
 fun decode(options:BitmapFactory.Options):Bitmap? =
  if(local) BitmapFactory.decodeFile(file.absolutePath,options)
  else context.assets.open("starter/posters/${file.name}").use {
   BitmapFactory.decodeStream(it,null,options)
  }
 return runCatching {
  val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
  decode(bounds)
  if(bounds.outWidth<=0 || bounds.outHeight<=0) return@runCatching null
  var sample=1
  while(bounds.outWidth/sample>640 || bounds.outHeight/sample>960) sample*=2
  decode(BitmapFactory.Options().apply {
   inSampleSize=sample
   inPreferredConfig=Bitmap.Config.RGB_565
  })
 }.getOrNull()
}

@Composable
fun ScoriumPoster(title:String,modifier:Modifier=Modifier,revision:Int=0,isCollection:Boolean=false,borderColor:Color=Color(0xFFB46AFF)) {
 val context=LocalContext.current
 val file=posterFile(context,title)
 val isLocal=file.isFile
 val stamp=if(isLocal) file.lastModified() else 0L
 val length=if(isLocal) file.length() else 0L
 val cacheKey = remember(file.absolutePath,isLocal,stamp,length) {
  if(isLocal) "${file.absolutePath}:$stamp:$length" else "asset:starter/posters/${file.name}"
 }
 var bitmap by remember(cacheKey,revision) { mutableStateOf(posterThumbnailCache.get(cacheKey)) }
 LaunchedEffect(cacheKey,revision) {
  if(bitmap == null) {
   val decoded = withContext(Dispatchers.IO) {
    posterThumbnailCache.get(cacheKey) ?: loadPosterThumbnail(context,file)?.also {
     posterThumbnailCache.put(cacheKey,it)
    }
   }
   bitmap = decoded
  }
 }
 val shape=RoundedCornerShape(9.dp)
 val effectiveBorder=if(LocalMcuCollectionTheme.current) mcuAccent else if(LocalDcCollectionTheme.current) dcAccent else if(LocalStarWarsCollectionTheme.current) Color(0xFFD7AB48) else borderColor
 Box(modifier.shadow(if(LocalMcuCollectionTheme.current) 10.dp else 6.dp,shape,ambientColor=effectiveBorder,spotColor=effectiveBorder).clip(shape).border(if(LocalMcuCollectionTheme.current) 2.dp else 1.5.dp,effectiveBorder,shape)) {
  if(bitmap!=null) {
   Image(bitmap=bitmap.asImageBitmap(),contentDescription="Bild: $title",
    modifier=Modifier.fillMaxSize().padding(1.5.dp).clip(RoundedCornerShape(7.5.dp)),
    contentScale=ContentScale.Crop)
  } else if(title=="collection::starwars" || title.startsWith("collection::starwars::")) {
   Image(painter=painterResource(R.drawable.scorium_star_wars_collection),contentDescription="STAR WARS",modifier=Modifier.fillMaxSize().padding(1.5.dp).clip(RoundedCornerShape(7.5.dp)),contentScale=ContentScale.Crop)
  } else if(title=="collection::dc" || title.startsWith("collection::dc::")) {
   Image(painter=painterResource(R.drawable.scorium_dc_card),contentDescription="DC STUDIOS",modifier=Modifier.fillMaxSize().padding(1.5.dp).clip(RoundedCornerShape(7.5.dp)),contentScale=ContentScale.Crop)
  } else if(title.startsWith("collection::mcu::")) {
   // Original uploaded 4:3 MCU cover, used only for the seven MCU phase tiles.
   Image(
    painter=painterResource(R.drawable.mcu_phase_cover),
    contentDescription="Marvel Studios MCU",
    modifier=Modifier.fillMaxSize().padding(1.5.dp).clip(RoundedCornerShape(7.5.dp)),
    contentScale=ContentScale.Crop
   )
  } else {
   Box(Modifier.fillMaxSize().padding(1.5.dp).clip(RoundedCornerShape(7.5.dp))
    .background(Color(0xFF080716)),contentAlignment=Alignment.Center) {
    Image(painter=painterResource(if(isCollection) R.drawable.scorium_collection else R.drawable.scorium_poster),
     contentDescription="SCORIUM Platzhalter für $title",
     modifier=Modifier.fillMaxSize(),contentScale=ContentScale.Crop)
   }
  }
 }
}

@Composable
fun ScoriumTitleCard(title:String,kind:String,score:Int?,grade:String?,onClick:()->Unit,imageKey:String=title,revision:Int=0) {
 val v=scoriumVisuals()
 val shape=RoundedCornerShape(20.dp)
 Card(onClick=onClick,shape=shape,
  colors=CardDefaults.cardColors(containerColor=v.surface),
  modifier=Modifier.fillMaxWidth().border(1.dp,v.border.copy(alpha=0.75f),shape)) {
  Row(Modifier.fillMaxWidth()
   .background(Brush.horizontalGradient(listOf(v.elevated,v.surface)))
   .padding(horizontal=13.dp,vertical=12.dp),verticalAlignment=Alignment.CenterVertically) {
   ScoriumPoster(imageKey,Modifier.size(width=72.dp,height=101.dp),revision)
   Spacer(Modifier.width(13.dp))
   Column(Modifier.weight(1f)) {
    Text(kind.uppercase(),fontSize=10.sp,letterSpacing=1.6.sp,color=v.accent,fontWeight=FontWeight.SemiBold)
    Spacer(Modifier.height(7.dp))
    Text(title,fontSize=16.sp,fontWeight=FontWeight.Bold,color=Color.White,
     maxLines=3,overflow=TextOverflow.Ellipsis,lineHeight=20.sp)
    ReleaseYears.label(title)?.let { years ->
     Spacer(Modifier.height(6.dp))
     Text(years,color=v.mutedText,fontSize=12.sp,fontStyle=FontStyle.Italic)
    }
    if(score==null) {
     Spacer(Modifier.height(10.dp))
     Text("NOCH NICHT BEWERTET",color=v.mutedText.copy(alpha=0.86f),
      fontSize=10.sp,letterSpacing=0.4.sp)
    }
   }
   Spacer(Modifier.width(6.dp))
   ScoriumScoreRing(score,63.dp)
  }
 }
}

@Composable
fun ScoriumRatingCard(label:String,value:Int?,onChange:(Int)->Unit) {
 val v=scoriumVisuals()
 val accent=v.accent
 // Read the latest callback during an ongoing gesture; do not restart the
 // pointer input handler every time a rating changes.
 val currentOnChange by rememberUpdatedState(onChange)
 Card(shape=RoundedCornerShape(20.dp),
  colors=CardDefaults.cardColors(containerColor=v.surface),
  modifier=Modifier.fillMaxWidth().border(1.dp,v.border.copy(alpha=0.85f),RoundedCornerShape(20.dp))) {
  Column(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(v.elevated,v.surface)))
   .padding(horizontal=15.dp,vertical=14.dp)) {
   Text(label,color=Color.White,fontWeight=FontWeight.Bold,fontSize=16.sp,lineHeight=21.sp)
   Spacer(Modifier.height(6.dp))
   Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Top,
    horizontalArrangement=Arrangement.spacedBy(8.dp)) {
    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(5.dp)) {
     ScoriumScoreRing(value?.times(10),60.dp,wholeNumber=true)
     Text(
      if(value==null) "Wähle eine Bewertung, um eine passende Einschätzung zu sehen."
      else criterionDescription(label,value),
      color=v.mutedText,fontSize=13.sp,lineHeight=18.sp,
      fontStyle=FontStyle.Italic,textAlign=TextAlign.Start,
      modifier=Modifier.fillMaxWidth()
     )
    }
    Column(horizontalAlignment=Alignment.CenterHorizontally) {
     Text("10",color=fog,fontSize=10.sp)
     val height=206.dp
     val gap=3.dp
     Box(
      Modifier.width(64.dp).height(height).pointerInput(label) {
       // Claim gestures beginning on the scale so the page never scrolls
       // instead of changing the score. Taps and sliding both work.
       awaitEachGesture {
        val down=awaitFirstDown(requireUnconsumed=false)
        var lastScore:Int?=null
        val segmentStep=(size.height.toFloat()+gap.toPx())/10f
        fun choose(y:Float) {
         val index=(y.coerceIn(0f,size.height.toFloat())/segmentStep).toInt().coerceIn(0,9)
         val score=10-index
         if(score!=lastScore) {
          lastScore=score
          currentOnChange(score)
         }
        }
        down.consume()
        choose(down.position.y)
        while(true) {
         val event=awaitPointerEvent()
         val change=event.changes.firstOrNull { it.id==down.id }
         if(change==null || !change.pressed) {
          change?.consume()
          break
         }
         change.consume()
         choose(change.position.y)
        }
       }
      },
      contentAlignment=Alignment.Center
     ) {
      Column(Modifier.fillMaxHeight().width(44.dp),
       verticalArrangement=Arrangement.spacedBy(gap)) {
       (10 downTo 1).forEach { n ->
        val segmentColor=when(n) {
         in 1..4 -> Color(0xFFED173B)
         5 -> Color(0xFFFF6538)
         6 -> Color(0xFFFF9828)
         7 -> Color(0xFFFFD633)
         8 -> Color(0xFF5DE348)
         9 -> Color(0xFF13C74D)
         else -> Color(0xFF007E32)
        }
        Box(Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(5.dp))
         .background(segmentColor.copy(alpha=if(value!=null && n<=value) 1f else 0.25f))
         .border(if(value==n) 2.dp else 0.dp,Color.White,RoundedCornerShape(5.dp)))
       }
      }
     }
     Text("1",color=fog,fontSize=10.sp)
    }
   }
  }
 }
}

@Composable
fun ScoriumDetailHero(title:String,kind:String,revision:Int=0,score:Int?=null) {
 val v=scoriumVisuals()
 val shape=RoundedCornerShape(24.dp)
 Card(shape=shape,colors=CardDefaults.cardColors(containerColor=v.surface),
  modifier=Modifier.fillMaxWidth().border(1.dp,v.border,shape)) {
  Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(v.elevated,v.surface)))
   .padding(horizontal=15.dp,vertical=17.dp),verticalAlignment=Alignment.CenterVertically) {
   ScoriumPoster(title,Modifier.size(width=100.dp,height=140.dp),revision)
   Spacer(Modifier.width(14.dp))
   Column(Modifier.weight(1f)) {
    Text(kind.uppercase(),color=v.accent,fontSize=11.sp,
     fontWeight=FontWeight.SemiBold,letterSpacing=1.5.sp)
    Spacer(Modifier.height(10.dp))
    Text(title,color=Color.White,fontSize=20.sp,fontWeight=FontWeight.Bold,
     maxLines=4,overflow=TextOverflow.Ellipsis,lineHeight=24.sp)
    ReleaseYears.label(title)?.let { years ->
     Spacer(Modifier.height(8.dp))
     Text(years,color=v.mutedText,fontSize=12.sp,fontStyle=FontStyle.Italic)
    }
   }
   Spacer(Modifier.width(5.dp))
   ScoriumScoreRing(score,68.dp)
  }
 }
}

@Composable
fun ScoriumMenuTile(label:String,subtitle:String,index:Int,onClick:()->Unit) {
 val v=scoriumVisuals()
 val shape=RoundedCornerShape(20.dp)
 Card(onClick=onClick,shape=shape,modifier=Modifier.fillMaxWidth().height(90.dp)
  .border(1.dp,v.border,shape),
  colors=CardDefaults.cardColors(containerColor=v.surface)) {
  Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(v.elevated,v.surface)))) {
   Box(Modifier.align(Alignment.CenterStart).padding(start=15.dp)
    .size(width=3.dp,height=45.dp).background(v.accent,RoundedCornerShape(4.dp)))
   Column(Modifier.align(Alignment.CenterStart).padding(start=31.dp)) {
    Text(label.uppercase(),fontSize=18.sp,fontWeight=FontWeight.Bold,
     color=Color.White,letterSpacing=1.sp)
    Spacer(Modifier.height(4.dp))
    Text(subtitle,fontSize=12.sp,color=v.mutedText)
   }
   Text("›",modifier=Modifier.align(Alignment.CenterEnd).padding(end=23.dp),
    fontSize=30.sp,color=v.accent)
  }
 }
}

@Composable
fun ScoriumCollectionTile(title:String,subtitle:String,imageKey:String,onClick:()->Unit,onImageSaved:()->Unit,revision:Int=0,onPosterBounds:((androidx.compose.ui.geometry.Rect)->Unit)?=null) {
 val v=scoriumVisuals()
 val shape=RoundedCornerShape(20.dp)
 Card(shape=shape,colors=CardDefaults.cardColors(containerColor=v.surface),
  modifier=Modifier.width(190.dp).border(1.dp,v.border.copy(alpha=0.85f),shape)) {
  Column(Modifier.background(Brush.verticalGradient(listOf(v.elevated,v.surface))).padding(9.dp)) {
   Box {
    ScoriumPoster(imageKey,Modifier.fillMaxWidth().aspectRatio(4f/3f)
     .onGloballyPositioned { onPosterBounds?.invoke(it.boundsInWindow()) }.clickable(onClick=onClick),
     revision,isCollection=true)
    Text("ÖFFNEN  ›",color=Color.White,fontWeight=FontWeight.Bold,fontSize=10.sp,
     letterSpacing=0.7.sp,modifier=Modifier.align(Alignment.BottomEnd)
      .background(v.inset.copy(alpha=0.94f),RoundedCornerShape(topStart=9.dp,bottomEnd=9.dp))
      .clickable(onClick=onClick).padding(horizontal=10.dp,vertical=8.dp))
   }
   Spacer(Modifier.height(12.dp))
   Text(title,color=Color.White,fontSize=16.sp,fontWeight=FontWeight.Bold,
    maxLines=2,modifier=Modifier.clickable(onClick=onClick))
   Spacer(Modifier.height(3.dp))
   Text(subtitle,color=v.mutedText,fontSize=12.sp,maxLines=1)
   if(BuildConfig.SCORIUM_ADMIN || !imageKey.startsWith("collection::mcu") &&
    !imageKey.startsWith("collection::dc") && !imageKey.startsWith("collection::starwars"))
    PosterPicker(imageKey,onImageSaved)
  }
 }
}
