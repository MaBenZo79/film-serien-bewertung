package de.benzo.scorium

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Shared reveal choreography across franchise hubs; original catalog keys stay intact. */
@Composable
fun McuPhaseHub(revision:Int,onImageSaved:()->Unit,onBack:()->Unit,onPhase:(String)->Unit) {
 val v=scoriumVisuals()
 val entries=listOf("Komplett","Phase 1","Phase 2","Phase 3","Phase 4","Phase 5","Phase 6")
 ScoriumHubPage {
  Column(
   Modifier.fillMaxSize()
    .background(Brush.verticalGradient(listOf(v.backdrop,v.surface,v.elevated,v.backdrop)))
    .statusBarsPadding().navigationBarsPadding()
    .verticalScroll(rememberScrollState())
    .padding(horizontal=18.dp)
  ) {
   Spacer(Modifier.height(16.dp))
   TextButton(onClick=onBack) { Text("← SCORIUM",color=v.mutedText) }
   Spacer(Modifier.height(18.dp))
   Image(
    painter=painterResource(R.drawable.scorium_mcu_logo),
    contentDescription="MARVEL STUDIOS MCU",
    contentScale=ContentScale.Fit,
    modifier=Modifier.fillMaxWidth().height(120.dp)
   )
   Spacer(Modifier.height(24.dp))
   entries.chunked(2).forEachIndexed { rowIndex,pair ->
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
     pair.forEachIndexed { columnIndex,phase ->
      Column(Modifier.weight(1f)) {
       ScoriumCardEntrance(index=rowIndex*2+columnIndex,startDelayMillis=330) {
        Column {
         Box(
          Modifier.fillMaxWidth().aspectRatio(4f/3f)
           .clip(RoundedCornerShape(14.dp))
           .border(1.5.dp,v.accent,RoundedCornerShape(14.dp))
           .clickable { onPhase(phase) }
         ) {
          ScoriumPoster(
           "collection::mcu::$phase",
           Modifier.fillMaxSize(),
           revision=revision,
           isCollection=true,
           borderColor=v.accent
          )
          Box(
           Modifier.align(Alignment.BottomStart).fillMaxWidth()
            .background(v.inset.copy(alpha=0.94f))
            .padding(horizontal=10.dp,vertical=7.dp)
          ) {
           Text(phase,color=Color.White,fontWeight=FontWeight.Bold,fontSize=15.sp)
          }
         }
         // Editing is a separate, sized row BELOW the thumbnail,
         // never floating over another card or on top of the artwork.
         if(BuildConfig.SCORIUM_ADMIN) {
          Row(Modifier.fillMaxWidth().padding(top=6.dp,bottom=5.dp),
           horizontalArrangement=Arrangement.End,
           verticalAlignment=Alignment.CenterVertically) {
           Text("Bild ändern",color=v.mutedText,fontSize=11.sp)
           Spacer(Modifier.width(5.dp))
           PosterPicker("collection::mcu::$phase",onImageSaved)
          }
         }
        }
       }
      }
     }
     if(pair.size==1) Spacer(Modifier.weight(1f))
    }
    Spacer(Modifier.height(12.dp))
   }
   Spacer(Modifier.height(22.dp))
  }
 }
}
