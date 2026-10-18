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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

private val ink=Color(0xFF090C1B)
private val slate=Color(0xFF1A203A)
private val gold=Color(0xFFAC88F6)
private val fog=Color(0xFFB5B7CD)

@Composable
fun ScoriumBackdrop(content: @Composable BoxScope.()->Unit) {
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
fun ScoriumPoster(title:String,modifier:Modifier=Modifier,revision:Int=0,isCollection:Boolean=false) {
 val context=LocalContext.current
 val file=posterFile(context,title)
 val stamp=file.lastModified()
 val bitmap=remember(title,stamp,revision) {
  if(file.exists()) runCatching { BitmapFactory.decodeFile(file.absolutePath) }.getOrNull() else null
 }
 val shape=RoundedCornerShape(9.dp)
 Box(modifier.shadow(6.dp,shape).clip(shape).border(1.5.dp,Color(0xFFB46AFF),shape)) {
  if(bitmap!=null) {
   Image(bitmap=bitmap.asImageBitmap(),contentDescription="Bild: $title",
    modifier=Modifier.fillMaxSize().padding(1.5.dp).clip(RoundedCornerShape(7.5.dp)),
    contentScale=ContentScale.Crop)
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
 Card(onClick=onClick,shape=RoundedCornerShape(18.dp),
  colors=CardDefaults.cardColors(containerColor=Color(0xFF182235)),
  modifier=Modifier.fillMaxWidth()) {
  Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
   ScoriumPoster(imageKey,Modifier.size(width=75.dp,height=100.dp),revision)
   Spacer(Modifier.width(15.dp))
   Column(Modifier.weight(1f)) {
    Text(kind.uppercase(),fontSize=10.sp,letterSpacing=1.5.sp,color=gold)
    Spacer(Modifier.height(6.dp))
    Text(title,fontSize=17.sp,fontWeight=FontWeight.Bold,color=Color.White,
     maxLines=3,overflow=TextOverflow.Ellipsis)
    Spacer(Modifier.height(9.dp))
    Text(if(score==null) "NOCH NICHT BEWERTET" else "${score / 10}.${score % 10}  ·  $grade",
     color=if(score==null) fog else gold,fontSize=12.sp,fontWeight=FontWeight.Bold)
   }
   Text("›",fontSize=28.sp,color=fog)
  }
 }
}

@Composable
fun ScoriumRatingCard(label:String,value:Int?,onChange:(Int)->Unit) {
 val animated by animateFloatAsState(targetValue=(value ?: 5).toFloat(),animationSpec=tween(230),label="score")
 Card(shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF171C32)),
  modifier=Modifier.fillMaxWidth()) {
  Column(Modifier.padding(horizontal=18.dp,vertical=20.dp)) {
   Text(label,color=Color.White,fontWeight=FontWeight.Bold,fontSize=18.sp)
   Spacer(Modifier.height(13.dp))
   Box(Modifier.fillMaxWidth().background(Color(0xFF252A3F),RoundedCornerShape(18.dp)).padding(18.dp),
    contentAlignment=Alignment.Center) {
    Column(horizontalAlignment=Alignment.CenterHorizontally) {
     Text(if(value==null) "– / 10" else "${animated.toInt()}/10",
      color=Color.White,fontSize=34.sp,fontWeight=FontWeight.Black)
     Spacer(Modifier.height(10.dp))
     Text(if(value==null) "Wähle eine Bewertung, um eine passende Einschätzung zu sehen."
       else criterionDescription(label,value),
      color=Color(0xFFE1DDED),fontSize=14.sp,lineHeight=21.sp,textAlign=TextAlign.Center)
    }
   }
   Spacer(Modifier.height(13.dp))
   var dragging by remember { mutableStateOf<Float?>(null) }
   Slider(value=dragging ?: (value ?: 5).toFloat(),onValueChange={ next ->
     dragging=next
     onChange(kotlin.math.round(next).toInt().coerceIn(1,10))
    },onValueChangeFinished={ dragging=null },
    valueRange=1f..10f,steps=0,
    colors=SliderDefaults.colors(thumbColor=gold,activeTrackColor=gold,
     inactiveTrackColor=Color(0xFF42445A)))
   Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
    Text("1",color=fog,fontSize=12.sp)
    Text("5",color=fog,fontSize=12.sp)
    Text("10",color=fog,fontSize=12.sp)
   }
  }
 }

}

@Composable
fun ScoriumDetailHero(title:String,kind:String,revision:Int=0) {
 Card(shape=RoundedCornerShape(24.dp),
  colors=CardDefaults.cardColors(containerColor=Color(0xFF19243A)),
  modifier=Modifier.fillMaxWidth()) {
  Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically) {
   ScoriumPoster(title,Modifier.size(width=112.dp,height=154.dp),revision)
   Spacer(Modifier.width(17.dp))
   Column(Modifier.weight(1f)) {
    Text(kind.uppercase(),color=gold,fontSize=11.sp,letterSpacing=1.5.sp)
    Spacer(Modifier.height(8.dp))
    Text(title,color=Color.White,fontSize=22.sp,fontWeight=FontWeight.Black,
     maxLines=4,overflow=TextOverflow.Ellipsis)
    Spacer(Modifier.height(8.dp))
    Text("DEIN PERSÖNLICHES ARCHIV",color=fog,fontSize=10.sp)
   }
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
fun ScoriumCollectionTile(title:String,subtitle:String,imageKey:String,onClick:()->Unit,onImageSaved:()->Unit,revision:Int=0) {
 Card(shape=RoundedCornerShape(19.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF1A233D)),modifier=Modifier.width(190.dp)) {
  Column(Modifier.padding(10.dp)) {
   Box {
    ScoriumPoster(imageKey,Modifier.fillMaxWidth().height(128.dp).clickable(onClick=onClick),revision,isCollection=true)
    Text("Öffnen ›",color=Color.White,fontSize=11.sp,modifier=Modifier.align(Alignment.BottomEnd).background(Color(0xCC111827),RoundedCornerShape(8.dp)).clickable(onClick=onClick).padding(6.dp))
   }
   Spacer(Modifier.height(10.dp))
   Text(title,color=Color.White,fontSize=16.sp,fontWeight=FontWeight.Bold,maxLines=2,modifier=Modifier.clickable(onClick=onClick))
   Text(subtitle,color=Color(0xFFB5B7CD),fontSize=12.sp,maxLines=1)
   PosterPicker(imageKey,onImageSaved)
  }
 }
}
