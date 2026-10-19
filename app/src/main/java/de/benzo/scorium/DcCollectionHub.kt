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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val dcBlue=Color(0xFF438FEA)
@Composable
fun DcCollectionHub(revision:Int,onImageSaved:()->Unit,onBack:()->Unit,onSection:(String)->Unit) {
 Column(Modifier.fillMaxSize().background(Color(0xFF06152D)).statusBarsPadding().navigationBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal=18.dp)) {
  Spacer(Modifier.height(16.dp))
  TextButton(onClick=onBack) { Text("← SCORIUM",color=Color.White) }
  Spacer(Modifier.height(22.dp))
  Image(painter=painterResource(R.drawable.scorium_dc_logo),contentDescription="DC STUDIOS",contentScale=ContentScale.Fit,modifier=Modifier.fillMaxWidth().height(120.dp))
  Spacer(Modifier.height(26.dp))
  listOf("Komplett","DCU","DCEU").chunked(2).forEach { pair ->
   Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
    pair.forEach { section ->
     Column(Modifier.weight(1f)) {
      Box(Modifier.fillMaxWidth().aspectRatio(4f/3f).clip(RoundedCornerShape(14.dp)).border(1.5.dp,dcBlue,RoundedCornerShape(14.dp)).clickable { onSection(section) }) {
       ScoriumPoster("collection::dc::$section",Modifier.fillMaxSize(),revision=revision,isCollection=true,borderColor=dcBlue)
       Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().background(Color(0xD006152D)).padding(horizontal=10.dp,vertical=7.dp)) {
        Text(section,color=Color.White,fontWeight=FontWeight.Bold,fontSize=16.sp)
       }
      }
      if(BuildConfig.SCORIUM_ADMIN) PosterPicker("collection::dc::$section",onImageSaved)
     }
    }
    if(pair.size==1) Spacer(Modifier.weight(1f))
   }
   Spacer(Modifier.height(16.dp))
  }
 }
}
