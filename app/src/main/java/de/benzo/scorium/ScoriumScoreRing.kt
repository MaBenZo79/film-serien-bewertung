package de.benzo.scorium

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private fun scoreColor(score:Int):Color = when {
 score>=90 -> Color(0xFF007E32)
 score>=76 -> Color(0xFF5DE348)
 score>=66 -> Color(0xFFFFD633)
 score>=56 -> Color(0xFFFF9828)
 score>=46 -> Color(0xFFFF6538)
 else -> Color(0xFFED173B)
}

private fun glowColor(score:Int):Color = when {
 score>=90 -> Color(0xFF00FF65)
 score>=76 -> Color(0xFF91FF50)
 score>=66 -> Color(0xFFFFFF36)
 score>=56 -> Color(0xFFFFC13A)
 score>=46 -> Color(0xFFFF8B48)
 else -> Color(0xFFFF2953)
}

@Composable
fun ScoriumScoreRing(score:Int?, diameter:Dp=76.dp,wholeNumber:Boolean=false) {
 val color=score?.let(::scoreColor) ?: Color(0xFF757587)
 val glow=score?.let(::glowColor) ?: Color(0xFFA0A0AF)
 val progress=score?.coerceIn(0,100)?.div(100f) ?: 0f
 val animated=animateFloatAsState(progress,tween(450),label="scoreRingProgress").value
 Box(Modifier.size(diameter),contentAlignment=Alignment.Center) {
  Canvas(Modifier.matchParentSize()) {
   val stroke=size.minDimension*0.095f
   val inset=stroke/2+2.dp.toPx()
   val arcSize=androidx.compose.ui.geometry.Size(size.width-2*inset,size.height-2*inset)
   val topLeft=androidx.compose.ui.geometry.Offset(inset,inset)
   drawArc(glow.copy(alpha=0.22f),-90f,360f,false,topLeft,arcSize,style=Stroke(stroke))
   if(animated>0f) {
    drawArc(glow.copy(alpha=0.27f),-90f,360f*animated,false,topLeft,arcSize,style=Stroke(stroke*1.65f,cap=StrokeCap.Round))
    drawArc(glow,-90f,360f*animated,false,topLeft,arcSize,style=Stroke(stroke,cap=StrokeCap.Round))
   }
  }
  Box(Modifier.size(diameter*0.73f).clip(CircleShape).background(color),contentAlignment=Alignment.Center) {
   Text(if(score==null) "–" else if(wholeNumber) "${score/10}" else "${score/10}.${score%10}",color=if(score==null) Color.White else Color.White,
    fontSize=if(diameter>=90.dp) 28.sp else 21.sp,fontWeight=FontWeight.Black)
  }
 }
}
