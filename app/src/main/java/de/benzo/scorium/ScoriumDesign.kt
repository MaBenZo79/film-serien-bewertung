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
 Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(
  Color(0xFF0B1528),Color(0xFF0C111D),ink)))) { content() }
}

@Composable
fun ScoriumHeader(subtitle:String="DEIN PERSÖNLICHES FILMARCHIV") {
 Column(Modifier.fillMaxWidth().padding(top=12.dp,bottom=10.dp)) {
  Image(painter=painterResource(R.drawable.scorium_logo),contentDescription="SCORIUM",modifier=Modifier.width(280.dp).height(66.dp),contentScale=ContentScale.Fit,alignment=Alignment.Center)
  Text(subtitle,fontSize=10.sp,letterSpacing=1.6.sp,color=fog)
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
fun ScoriumPoster(title:String,modifier:Modifier=Modifier) {
 val palette=listOf(
  listOf(Color(0xFF752A42),Color(0xFF182B48)),
  listOf(Color(0xFF234E67),Color(0xFF0E182F)),
  listOf(Color(0xFF66502B),Color(0xFF211C2F)),
  listOf(Color(0xFF513766),Color(0xFF171B36))
 )[Math.floorMod(title.hashCode(),4)]
 val initials=title.split(" ").filter { it.isNotBlank() }.take(2).map { it.first() }.joinToString("")
 Box(modifier.background(Brush.linearGradient(palette),RoundedCornerShape(12.dp))
  .border(1.dp,Color(0x35FFFFFF),RoundedCornerShape(12.dp)),contentAlignment=Alignment.Center) {
  Text(initials,fontSize=24.sp,fontWeight=FontWeight.Black,color=Color(0xDDE8D2AB))
 }
}

@Composable
fun ScoriumTitleCard(title:String,kind:String,score:Int?,grade:String?,onClick:()->Unit) {
 Card(onClick=onClick,shape=RoundedCornerShape(18.dp),
  colors=CardDefaults.cardColors(containerColor=Color(0xFF182235)),
  modifier=Modifier.fillMaxWidth()) {
  Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
   ScoriumPoster(title,Modifier.size(width=75.dp,height=100.dp))
   Spacer(Modifier.width(15.dp))
   Column(Modifier.weight(1f)) {
    Text(kind.uppercase(),fontSize=10.sp,letterSpacing=1.5.sp,color=gold)
    Spacer(Modifier.height(6.dp))
    Text(title,fontSize=17.sp,fontWeight=FontWeight.Bold,color=Color.White,
     maxLines=3,overflow=TextOverflow.Ellipsis)
    Spacer(Modifier.height(9.dp))
    Text(if(score==null) "NOCH NICHT BEWERTET" else "$score%  ·  $grade",
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
   Slider(value=(value ?: 5).toFloat(),onValueChange={onChange(it.toInt().coerceIn(1,10))},
    valueRange=1f..10f,steps=8,
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
fun ScoriumDetailHero(title:String,kind:String) {
 Card(shape=RoundedCornerShape(24.dp),
  colors=CardDefaults.cardColors(containerColor=Color(0xFF19243A)),
  modifier=Modifier.fillMaxWidth()) {
  Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically) {
   ScoriumPoster(title,Modifier.size(width=112.dp,height=154.dp))
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
