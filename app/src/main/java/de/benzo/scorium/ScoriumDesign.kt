package de.benzo.scorium

import androidx.compose.animation.core.*
import android.content.Context
import android.graphics.BitmapFactory
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
private val gold=Color(0xFFAC88F6)
private val fog=Color(0xFFB5B7CD)

val LocalMcuCollectionTheme = compositionLocalOf { false }
private val mcuAccent = Color(0xFFEF3945)
private val mcuSurface = Color(0xFF30090F)

@Composable
fun ScoriumBackdrop(content: @Composable BoxScope.()->Unit) {
 val mcu=LocalMcuCollectionTheme.current
 if(mcu) {
  Box(Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFF150306),Color(0xFF39070C),Color(0xFF110306))))) { content() }
  return
 }
 // Unified background for the home screen and all nested pages.
 // Keep the existing poster tiles, cards, controls and their colors intact.
 Box(Modifier.fillMaxSize().background(Color(0xFF010007))) {
  Image(
   painter = painterResource(R.drawable.scorium_menu_background),
   contentDescription = null,
   contentScale = ContentScale.FillBounds,
   modifier = Modifier.fillMaxSize()
  )
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

@Composable
fun ScoriumPoster(title:String,modifier:Modifier=Modifier,revision:Int=0,isCollection:Boolean=false,borderColor:Color=Color(0xFFB46AFF)) {
 val context=LocalContext.current
 val file=posterFile(context,title)
 val stamp=file.lastModified()
 val bitmap=remember(title,stamp,revision) {
  if(file.exists()) runCatching { BitmapFactory.decodeFile(file.absolutePath) }.getOrNull() else null
 }
 val shape=RoundedCornerShape(9.dp)
 val effectiveBorder=if(LocalMcuCollectionTheme.current) mcuAccent else borderColor
 Box(modifier.shadow(if(LocalMcuCollectionTheme.current) 10.dp else 6.dp,shape,ambientColor=effectiveBorder,spotColor=effectiveBorder).clip(shape).border(if(LocalMcuCollectionTheme.current) 2.dp else 1.5.dp,effectiveBorder,shape)) {
  if(bitmap!=null) {
   Image(bitmap=bitmap.asImageBitmap(),contentDescription="Bild: $title",
    modifier=Modifier.fillMaxSize().padding(1.5.dp).clip(RoundedCornerShape(7.5.dp)),
    contentScale=ContentScale.Crop)
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
 val mcu=LocalMcuCollectionTheme.current
 val accent=if(mcu) mcuAccent else gold
 Card(onClick=onClick,shape=RoundedCornerShape(18.dp),
  colors=CardDefaults.cardColors(containerColor=if(mcu) mcuSurface else Color(0xFF182235)),
  modifier=Modifier.fillMaxWidth().then(if(mcu) Modifier.border(1.5.dp,mcuAccent.copy(alpha=0.85f),RoundedCornerShape(18.dp)) else Modifier)) {
  Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
   ScoriumPoster(imageKey,Modifier.size(width=75.dp,height=100.dp),revision)
   Spacer(Modifier.width(15.dp))
   Column(Modifier.weight(1f)) {
    Text(kind.uppercase(),fontSize=10.sp,letterSpacing=1.5.sp,color=accent)
    Spacer(Modifier.height(6.dp))
    Text(title,fontSize=17.sp,fontWeight=FontWeight.Bold,color=Color.White,
     maxLines=3,overflow=TextOverflow.Ellipsis)
    ReleaseYears.label(title)?.let { years ->
     Spacer(Modifier.height(3.dp))
     Text(years,color=Color(0xFFAEB0BA),fontSize=12.sp,fontWeight=FontWeight.Normal,fontStyle=FontStyle.Italic)
    }
    if(score==null) { Spacer(Modifier.height(9.dp)); Text("NOCH NICHT BEWERTET",color=fog,fontSize=11.sp) }
   }
   ScoriumScoreRing(score,76.dp)
  }
 }
}

@Composable
fun ScoriumRatingCard(label:String,value:Int?,onChange:(Int)->Unit) {
 val mcu=LocalMcuCollectionTheme.current
 val accent=if(mcu) mcuAccent else gold
 val color=when(value ?: 0) {
  in 1..4 -> Color(0xFFED173B)
  5 -> Color(0xFFFF6538)
  6 -> Color(0xFFFF9828)
  7 -> Color(0xFFFFD633)
  8 -> Color(0xFF5DE348)
  9 -> Color(0xFF13C74D)
  10 -> Color(0xFF007E32)
  else -> accent
 }
 Card(shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=if(mcu) mcuSurface else Color(0xFF171C32)),
  modifier=Modifier.fillMaxWidth().then(if(mcu) Modifier.border(1.5.dp,mcuAccent.copy(alpha=0.8f),RoundedCornerShape(22.dp)) else Modifier)) {
  Column(Modifier.padding(16.dp)) {
   Text(label,color=Color.White,fontWeight=FontWeight.Bold,fontSize=18.sp)
   Spacer(Modifier.height(12.dp))
   Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(10.dp)) {
     ScoriumScoreRing(value?.times(10),70.dp,wholeNumber=true)
     Text(if(value==null) "Wähle eine Bewertung, um eine passende Einschätzung zu sehen."
      else criterionDescription(label,value),
      color=Color(0xFFE1DDED),fontSize=13.sp,lineHeight=19.sp,textAlign=TextAlign.Justify,
      modifier=Modifier.fillMaxWidth().background(Color(0xFF252A3F),RoundedCornerShape(14.dp)).padding(12.dp))
    }
    Column(horizontalAlignment=Alignment.CenterHorizontally) {
     Text("10",color=fog,fontSize=11.sp)
     val height=228.dp
     Box(Modifier.width(56.dp).height(height).pointerInput(label) {
      awaitPointerEventScope {
       while(true) {
        val event=awaitPointerEvent()
        val change=event.changes.firstOrNull() ?: continue
        if(change.pressed && change.position.y in 0f..size.height.toFloat()) {
         val n=(10-kotlin.math.floor(change.position.y/size.height*10).toInt()).coerceIn(1,10)
         if(n!=value) onChange(n)
        }
       }
      }
     },contentAlignment=Alignment.Center) {
      Column(Modifier.fillMaxHeight().width(38.dp),verticalArrangement=Arrangement.spacedBy(3.dp)) {
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
     Text("1",color=fog,fontSize=11.sp)
    }
   }
  }
 }
}

@Composable
fun ScoriumDetailHero(title:String,kind:String,revision:Int=0,score:Int?=null) {
 val mcu=LocalMcuCollectionTheme.current
 val accent=if(mcu) mcuAccent else gold
 Card(shape=RoundedCornerShape(24.dp),
  colors=CardDefaults.cardColors(containerColor=if(mcu) mcuSurface else Color(0xFF19243A)),
  modifier=Modifier.fillMaxWidth().then(if(mcu) Modifier.border(1.5.dp,mcuAccent, RoundedCornerShape(24.dp)) else Modifier)) {
  Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically) {
   ScoriumPoster(title,Modifier.size(width=112.dp,height=154.dp),revision)
   Spacer(Modifier.width(17.dp))
   Column(Modifier.weight(1f)) {
    Text(kind.uppercase(),color=accent,fontSize=11.sp,letterSpacing=1.5.sp)
    Spacer(Modifier.height(8.dp))
    Text(title,color=Color.White,fontSize=22.sp,fontWeight=FontWeight.Black,
     maxLines=4,overflow=TextOverflow.Ellipsis)
    ReleaseYears.label(title)?.let { years -> Spacer(Modifier.height(5.dp)); Text(years,color=fog,fontSize=12.sp,fontStyle=FontStyle.Italic) }
   }
   ScoriumScoreRing(score,76.dp)
  }
 }
}

@Composable
fun ScoriumMenuTile(label:String,subtitle:String,index:Int,onClick:()->Unit) {
 val transition=rememberInfiniteTransition(label="menu-$index")
 val shimmer by transition.animateFloat(0f,1f,
  infiniteRepeatable(tween(3200+index*300,easing=EaseInOutSine),RepeatMode.Reverse),label="shimmer")
 Card(onClick=onClick,shape=RoundedCornerShape(19.dp),modifier=Modifier.fillMaxWidth().height(90.dp),
  colors=CardDefaults.cardColors(containerColor=Color(0xFF1A2639))) {
  Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(
   Color(0xFF19273B),Color(0xFF26364D).copy(alpha=0.75f+shimmer*0.25f),
   Color(0xFF151E30))))) {
   Box(Modifier.align(Alignment.CenterStart).padding(start=14.dp)
    .size(width=3.dp,height=48.dp).background(gold,RoundedCornerShape(3.dp)))
   Column(Modifier.align(Alignment.CenterStart).padding(start=30.dp)) {
    Text(label.uppercase(),fontSize=20.sp,fontWeight=FontWeight.Bold,color=Color.White,letterSpacing=1.sp)
    Text(subtitle,fontSize=11.sp,color=fog)
   }
   Text("›",modifier=Modifier.align(Alignment.CenterEnd).padding(end=23.dp),
    fontSize=30.sp,color=gold)
  }
 }
}

@Composable
fun ScoriumCollectionTile(title:String,subtitle:String,imageKey:String,onClick:()->Unit,onImageSaved:()->Unit,revision:Int=0,onPosterBounds:((androidx.compose.ui.geometry.Rect)->Unit)?=null) {
 Card(shape=RoundedCornerShape(19.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF1A233D)),modifier=Modifier.width(190.dp)) {
  Column(Modifier.padding(10.dp)) {
   Box {
    ScoriumPoster(imageKey,Modifier.fillMaxWidth().height(128.dp).onGloballyPositioned { onPosterBounds?.invoke(it.boundsInWindow()) }.clickable(onClick=onClick),revision,isCollection=true)
    Text("Öffnen ›",color=Color.White,fontSize=11.sp,modifier=Modifier.align(Alignment.BottomEnd).background(Color(0xCC111827),RoundedCornerShape(8.dp)).clickable(onClick=onClick).padding(6.dp))
   }
   Spacer(Modifier.height(10.dp))
   Text(title,color=Color.White,fontSize=16.sp,fontWeight=FontWeight.Bold,maxLines=2,modifier=Modifier.clickable(onClick=onClick))
   Text(subtitle,color=Color(0xFFB5B7CD),fontSize=12.sp,maxLines=1)
   PosterPicker(imageKey,onImageSaved)
  }
 }
}
