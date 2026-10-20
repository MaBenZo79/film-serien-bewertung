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
fun StarWarsCollectionHub(revision:Int,onImageSaved:()->Unit,onBack:()->Unit,onSection:(String)->Unit) {
 val v=scoriumVisuals()
 val entries=listOf("Komplett","Original Trilogie","Prequel Trilogie","Sequel Trilogie","Zusatz Filme")
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
    painter=painterResource(R.drawable.scorium_star_wars_logo),
    contentDescription="STAR WARS",
    contentScale=ContentScale.Fit,
    modifier=Modifier.fillMaxWidth().height(120.dp)
   )
   Spacer(Modifier.height(24.dp))
   entries.chunked(2).forEachIndexed { rowIndex,pair ->
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
     pair.forEachIndexed { columnIndex,section ->
      Column(Modifier.weight(1f)) {
       ScoriumCardEntrance(index=rowIndex*2+columnIndex,startDelayMillis=520) {
        Column {
         Box(
          Modifier.fillMaxWidth().aspectRatio(4f/3f)
           .clip(RoundedCornerShape(14.dp))
           .border(1.5.dp,v.accent,RoundedCornerShape(14.dp))
           .clickable { onSection(section) }
         ) {
          // Full, uncropped collection poster. Label and editor are outside it.
          ScoriumPoster(
           "collection::starwars::$section",
           Modifier.fillMaxSize(),
           revision=revision,
           isCollection=true,
           borderColor=v.accent
          )
         }
         Row(
          Modifier.fillMaxWidth().heightIn(min=41.dp).padding(top=6.dp,bottom=3.dp),
          verticalAlignment=Alignment.CenterVertically,
          horizontalArrangement=Arrangement.SpaceBetween
         ) {
          Text(
           section,modifier=Modifier.weight(1f),
           color=Color.White,fontWeight=FontWeight.Bold,
           fontSize=14.sp,maxLines=2,lineHeight=17.sp
          )
          if(BuildConfig.SCORIUM_ADMIN) {
           // Pencil button sits beside the phase title, not on the artwork.
           PosterPicker("collection::starwars::$section",onImageSaved)
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
