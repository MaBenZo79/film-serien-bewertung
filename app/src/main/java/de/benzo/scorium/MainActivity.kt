package de.benzo.scorium

import android.app.Activity
import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import org.json.JSONArray

private val gold = Color(0xFFE4C477)
private val navy = Color(0xFF0D111B)
private val categories = listOf("Handlung & Drehbuch","Charaktere & Entwicklung","Antagonist & Konflikt","Schauspiel & Dialoge","Spannung & Erzähltempo","Action & Inszenierung","Visuelle Qualität & Effekte","Musik & Sounddesign","Emotionale Wirkung & Atmosphäre","Langlebigkeit & Zeitlosigkeit")
private val seasonCategories = categories + "Sogwirkung & Dranbleibfaktor"
private val episodeCategories = listOf("Handlung & Drehbuch","Charaktere & Interaktionen","Spannung & Erzähltempo","Regie & Inszenierung","Visuelle Qualität & Effekte","Musik & Sounddesign","Emotionale Wirkung & Atmosphäre","Episodenabschluss & Fortsetzungsreiz")
private fun scoreDescription(n: Int) = when(n) {
 in 1..2 -> "Sehr schwach"
 in 3..4 -> "Unterdurchschnittlich"
 in 5..6 -> "Solide, mit Schwächen"
 in 7..8 -> "Sehr gelungen"
 else -> "Herausragend"
}
private val films = listOf("Iron Man","Der unglaubliche Hulk","Iron Man 2","Thor","Captain America: The First Avenger","Marvel's The Avengers","Iron Man 3","Thor: The Dark Kingdom","Captain America: The Winter Soldier","Guardians of the Galaxy","Avengers: Age of Ultron","Ant-Man","Captain America: Civil War","Doctor Strange","Guardians of the Galaxy Vol. 2","Spider-Man: Homecoming","Thor: Ragnarok","Black Panther","Avengers: Infinity War","Ant-Man and the Wasp","Captain Marvel","Avengers: Endgame","Spider-Man: Far From Home")
private val series = listOf("WandaVision","The Falcon and the Winter Soldier","Loki","What If...?","Hawkeye","Moon Knight","Ms. Marvel","She-Hulk","Secret Invasion","Echo","Agatha All Along","Daredevil: Born Again")
private fun grade(p: Int) = when { p>=95->"A+";p>=90->"A";p>=85->"A-";p>=80->"B+";p>=75->"B";p>=70->"B-";p>=65->"C+";p>=60->"C";p>=55->"C-";p>=50->"D+";p>=45->"D";p>=40->"D-";else->"F" }
class MainActivity: ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val prefs=getSharedPreferences("ratings",Context.MODE_PRIVATE)
  setContent {
   var selected by remember { mutableStateOf<String?>(null) }
   var selectedSeason by remember { mutableIntStateOf(0) }
   var selectedEpisode by remember { mutableIntStateOf(0) }
   var seasonCount by remember { mutableIntStateOf(1) }
   var episodeCount by remember { mutableIntStateOf(6) }
   var search by remember { mutableStateOf("") }
   var tab by remember { mutableStateOf("Alle") }
   val extras=remember {
    mutableStateListOf<String>().apply {
     val data=runCatching { JSONArray(prefs.getString("custom_titles","[]")) }.getOrDefault(JSONArray())
     for(i in 0 until data.length()) add(data.optString(i))
    }
   }
   var add by remember { mutableStateOf(false) }
   var newTitle by remember { mutableStateOf("") }
   var newKind by remember { mutableStateOf("Film") }
   val customSeries=remember {
    mutableStateListOf<String>().apply {
     val data=runCatching { JSONArray(prefs.getString("custom_series","[]")) }.getOrDefault(JSONArray())
     for(i in 0 until data.length()) add(data.optString(i))
    }
   }
   val palette=darkColorScheme(primary=gold,background=navy,surface=Color(0xFF1B2534),onSurface=Color.White,onBackground=Color.White)
   MaterialTheme(colorScheme=palette) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
     Text("SCORIUM",fontSize=32.sp,color=gold)
     Text("Dein Film- und Serienarchiv",fontSize=13.sp)
     Spacer(Modifier.height(16.dp))
     if(selected==null) {
      OutlinedTextField(search,{search=it},label={Text("Suchen")},modifier=Modifier.fillMaxWidth())
      Row { listOf("Alle","Filme","Serien").forEach { name -> FilterChip(selected=tab==name,onClick={tab=name},label={Text(name)});Spacer(Modifier.width(6.dp)) } }
      val entries=((if(tab=="Serien") emptyList() else films+extras)+(if(tab=="Filme") emptyList() else series+customSeries)).filter { it.contains(search,true) }
      LazyColumn(Modifier.weight(1f)) {
       items(entries) { name ->
        Card(onClick={selected=name;selectedSeason=0;selectedEpisode=0},modifier=Modifier.fillMaxWidth().padding(vertical=5.dp)) {
         Column(Modifier.padding(16.dp)) {
          Text(name,fontSize=18.sp)
          Text(if(name in series || name in customSeries) "Serie" else "Film",color=gold,fontSize=12.sp)
         }
        }
       }
      }
      Button(onClick={add=true},modifier=Modifier.fillMaxWidth()) { Text("Titel hinzufügen") }
      if(add) AlertDialog(onDismissRequest={add=false},title={Text("Neuer Titel")},text={
       Column {
        OutlinedTextField(newTitle,{newTitle=it},label={Text("Name")})
        Row { listOf("Film","Serie").forEach { type ->
         FilterChip(selected=newKind==type,onClick={newKind=type},label={Text(type)})
         Spacer(Modifier.width(8.dp))
        } }
       }
      },confirmButton={TextButton(onClick={if(newTitle.isNotBlank()){
       val name=newTitle.trim()
       if(newKind=="Serie"){
        if(name !in customSeries && name !in series) customSeries.add(name)
        val data=JSONArray();customSeries.forEach { data.put(it) }
        prefs.edit().putString("custom_series",data.toString()).apply()
       } else {
        if(name !in extras && name !in films) extras.add(name)
        val data=JSONArray();extras.forEach { data.put(it) }
        prefs.edit().putString("custom_titles",data.toString()).apply()
       }
       newTitle=""
      };add=false}){Text("Hinzufügen")}},
       dismissButton={TextButton(onClick={add=false}){Text("Abbrechen")}})
     } else {
      val isSeries=selected!! in series || selected!! in customSeries
      BackHandler {
       when { selectedEpisode>0 -> selectedEpisode=0;selectedSeason>0 -> selectedSeason=0;else -> selected=null }
      }
      TextButton(onClick={
       when { selectedEpisode>0 -> selectedEpisode=0;selectedSeason>0 -> selectedSeason=0;else -> selected=null }
      }) { Text("← Zurück") }
      Text(selected!!,fontSize=24.sp)
      if(isSeries && selectedSeason==0) {
       Text("Staffeln",color=gold,fontSize=18.sp)
       LazyColumn(Modifier.weight(1f)) {
        items((1..seasonCount).toList()) { number ->
         Card(onClick={selectedSeason=number},modifier=Modifier.fillMaxWidth().padding(vertical=5.dp)) {
          Text("Staffel $number",Modifier.padding(18.dp))
         }
        }
       }
       Button(onClick={seasonCount++},modifier=Modifier.fillMaxWidth()) { Text("Staffel hinzufügen") }
      } else if(isSeries && selectedEpisode==0) {
       Text("Staffel $selectedSeason",color=gold,fontSize=18.sp)
       Row {
        Button(onClick={selectedEpisode=-1}) { Text("Staffel bewerten") }
       }
       Text("Episoden",color=gold)
       LazyColumn(Modifier.weight(1f)) {
        items((1..episodeCount).toList()) { number ->
         Card(onClick={selectedEpisode=number},modifier=Modifier.fillMaxWidth().padding(vertical=4.dp)) {
          Text("Episode $number",Modifier.padding(16.dp))
         }
        }
       }
       Button(onClick={episodeCount++},modifier=Modifier.fillMaxWidth()) { Text("Episode hinzufügen") }
      } else {
       val key=selected!! + if(!isSeries) "" else if(selectedEpisode == -1) "::season:$selectedSeason" else "::season:$selectedSeason:episode:$selectedEpisode"
       val criteria=if(!isSeries) categories else if(selectedEpisode == -1) seasonCategories else episodeCategories
       if(isSeries) Text(if(selectedEpisode == -1) "Staffel $selectedSeason bewerten" else "Episode $selectedEpisode bewerten",color=gold)
       val ratings=remember(key) {
        val json=runCatching { JSONObject(prefs.getString(key,"{}") ?: "{}") }.getOrDefault(JSONObject())
        mutableStateMapOf<String,Int>().apply { criteria.forEach { if(json.has(it)) put(it,json.optInt(it)) } }
       }
       val percent=if(criteria.all { ratings.containsKey(it) }) criteria.sumOf { ratings[it] ?: 0 } * 10 / criteria.size else null
       Text(if(percent==null) "Bewertet: ${ratings.size}/${criteria.size}" else "Gesamt: ${percent}% · ${grade(percent)}",color=gold,fontSize=19.sp)
       LazyColumn {
        items(criteria) { criterion ->
         Card(Modifier.fillMaxWidth().padding(vertical=5.dp)) {
          Column(Modifier.padding(14.dp)) {
           Text(criterion)
           val value=ratings[criterion]
           Text(value?.let { "${it}/10 · ${scoreDescription(it)}" } ?: "Noch nicht bewertet",color=gold)
           Slider(value=(value ?: 5).toFloat(),onValueChange={
            ratings[criterion]=it.toInt().coerceIn(1,10)
            val json=JSONObject();ratings.forEach { (name,score)->json.put(name,score) }
            prefs.edit().putString(key,json.toString()).apply()
           },valueRange=1f..10f,steps=8)
          }
         }
        }
       }
      }
     }
    }
   }
  }
 }
}
