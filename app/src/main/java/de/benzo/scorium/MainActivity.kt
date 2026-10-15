package de.benzo.scorium

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
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
private fun savedPercent(prefs: android.content.SharedPreferences, key: String, names: List<String>): Int? {
 val json=runCatching { JSONObject(prefs.getString(key,"{}") ?: "{}") }.getOrDefault(JSONObject())
 if(!names.all { json.has(it) }) return null
 return kotlin.math.round(names.sumOf { json.optInt(it) }.toDouble() * 10 / names.size).toInt()
}
private fun seasonAverage(prefs: android.content.SharedPreferences, seriesName: String, season: Int): Int? {
 val count=prefs.getInt("episodes::$seriesName::$season",McuCatalog.find(seriesName)?.seasons?.getOrNull(season-1) ?: 6).coerceAtLeast(1)
 val results=(1..count).mapNotNull { ep -> savedPercent(prefs,"$seriesName::season:$season:episode:$ep",episodeCategories) }
 return if(results.size==count) kotlin.math.round(results.average()).toInt() else null
}
private fun seasonScore(prefs: android.content.SharedPreferences, seriesName: String, season: Int): Int? {
 val manual=savedPercent(prefs,"$seriesName::season:$season",seasonCategories)
 val average=seasonAverage(prefs,seriesName,season)
 return if(prefs.getString("mode::$seriesName::$season","manual")=="episodes") average ?: manual else manual ?: average
}
private val legacyFilms = listOf("Iron Man","Der unglaubliche Hulk","Iron Man 2","Thor","Captain America: The First Avenger","Marvel's The Avengers","Iron Man 3","Thor: The Dark Kingdom","Captain America: The Winter Soldier","Guardians of the Galaxy","Avengers: Age of Ultron","Ant-Man","Captain America: Civil War","Doctor Strange","Guardians of the Galaxy Vol. 2","Spider-Man: Homecoming","Thor: Ragnarok","Black Panther","Avengers: Infinity War","Ant-Man and the Wasp","Captain Marvel","Avengers: Endgame","Spider-Man: Far From Home")
private val legacySeries = listOf("WandaVision","The Falcon and the Winter Soldier","Loki","What If...?","Hawkeye","Moon Knight","Ms. Marvel","She-Hulk","Secret Invasion","Echo","Agatha All Along","Daredevil: Born Again")
private fun grade(p: Int) = when { p>=95->"A+";p>=90->"A";p>=85->"A-";p>=80->"B+";p>=75->"B";p>=70->"B-";p>=65->"C+";p>=60->"C";p>=55->"C-";p>=50->"D+";p>=45->"D";p>=40->"D-";else->"F" }
private val films get() = McuCatalog.films
private val series get() = McuCatalog.shows.map { it.title }
class MainActivity: ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val prefs=getSharedPreferences("ratings",Context.MODE_PRIVATE)
  setContent {
   var selected by remember { mutableStateOf<String?>(null) }
   var home by remember { mutableStateOf(true) }
   var showExitDialog by remember { mutableStateOf(false) }
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
   var refresh by remember { mutableIntStateOf(0) }
   var exportReport by remember { mutableStateOf<RatingExport.Report?>(null) }
   var exportError by remember { mutableStateOf<String?>(null) }
   val pngLauncher=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
    if(uri!=null) runCatching {
     contentResolver.openOutputStream(uri)?.use { stream ->
      RatingExport.png(requireNotNull(exportReport),stream)
     } ?: error("Ausgabedatei nicht geöffnet")
    }.onFailure { exportError=it.localizedMessage }
   }
   val pdfLauncher=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
    if(uri!=null) runCatching {
     contentResolver.openOutputStream(uri)?.use { stream ->
      RatingExport.pdf(requireNotNull(exportReport),stream)
     } ?: error("Ausgabedatei nicht geöffnet")
    }.onFailure { exportError=it.localizedMessage }
   }
   val customSeries=remember {
    mutableStateListOf<String>().apply {
     val data=runCatching { JSONArray(prefs.getString("custom_series","[]")) }.getOrDefault(JSONArray())
     for(i in 0 until data.length()) add(data.optString(i))
    }
   }
   val palette=darkColorScheme(primary=gold,background=navy,surface=Color(0xFF182235),onSurface=Color.White,onBackground=Color.White,onPrimary=navy,onSurfaceVariant=Color(0xFFB6C4D5))
   BackHandler(enabled=true) {
    when {
     selected!=null && selectedEpisode!=0 -> selectedEpisode=0
     selected!=null && selectedSeason>0 -> selectedSeason=0
     selected!=null -> selected=null
     !home -> home=true
     else -> showExitDialog=true
    }
   }
   MaterialTheme(colorScheme=palette) {
    if(showExitDialog) AlertDialog(
     onDismissRequest={showExitDialog=false},
     title={Text("SCORIUM beenden?")},
     text={Text("Möchtest du die App wirklich schließen?")},
     confirmButton={TextButton(onClick={showExitDialog=false;this@MainActivity.finish()}) { Text("Ja, beenden") }},
     dismissButton={TextButton(onClick={showExitDialog=false}) { Text("Nein, bleiben") }}
    )
    if(exportError!=null) AlertDialog(onDismissRequest={exportError=null},title={Text("Exportfehler")},text={Text(exportError ?: "")},confirmButton={TextButton(onClick={exportError=null}){Text("OK")}})
    ScoriumBackdrop { Column(Modifier.fillMaxSize().padding(horizontal=18.dp)) {
     ScoriumHeader()
     Spacer(Modifier.height(12.dp))
     if(home) {
      Spacer(Modifier.height(10.dp))
      Text("DEINE FILMWELT",fontSize=12.sp,color=gold,fontWeight=FontWeight.Bold)
      Spacer(Modifier.height(8.dp))
      Text("Jede Geschichte verdient eine Wertung.",fontSize=25.sp,color=Color.White,fontWeight=FontWeight.Bold)
      Spacer(Modifier.height(24.dp))
      ScoriumHero(onExplore={home=false;tab="Alle"})
      Spacer(Modifier.height(22.dp))
      Text("DEIN ARCHIV",fontSize=12.sp,color=gold,fontWeight=FontWeight.Bold)
      Spacer(Modifier.height(14.dp))
      Button(onClick={home=false;tab="Filme"},modifier=Modifier.fillMaxWidth().height(54.dp)) { Text("FILME ENTDECKEN") }
      Spacer(Modifier.height(8.dp))
      OutlinedButton(onClick={home=false;tab="Serien"},modifier=Modifier.fillMaxWidth().height(54.dp)) { Text("SERIEN ENTDECKEN") }
     } else if(selected==null) {
      TextButton(onClick={home=true}) { Text("← Startseite") }
      OutlinedTextField(search,{search=it},label={Text("Suchen")},modifier=Modifier.fillMaxWidth())
      Row { listOf("Alle","Filme","Serien").forEach { name -> FilterChip(selected=tab==name,onClick={tab=name},label={Text(name)});Spacer(Modifier.width(6.dp)) } }
      val entries=((if(tab=="Serien") emptyList() else films+extras)+(if(tab=="Filme") emptyList() else series+customSeries)).filter { it.contains(search,true) }
      val revision=refresh
      LazyColumn(Modifier.weight(1f)) {
       items(entries) { name ->
        val isShow=name in series || name in customSeries
        val value=if(isShow) {
         val count=prefs.getInt("seasons::$name",McuCatalog.find(name)?.seasons?.size ?: 1)
         val scores=(1..count).mapNotNull { seasonScore(prefs,name,it) }
         if(scores.size==count) kotlin.math.round(scores.average()).toInt() else null
        } else savedPercent(prefs,name,categories)
        ScoriumTitleCard(name,if(isShow) "Serie" else "Film",value,value?.let { grade(it) }) {
         selected=name;selectedSeason=0;selectedEpisode=0
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
      val seriesKey=selected!!
      LaunchedEffect(seriesKey) {
       seasonCount=prefs.getInt("seasons::$seriesKey",McuCatalog.find(seriesKey)?.seasons?.size ?: 1).coerceAtLeast(1)
       episodeCount=prefs.getInt("episodes::$seriesKey::1",McuCatalog.find(seriesKey)?.seasons?.firstOrNull() ?: 6).coerceAtLeast(1)
      }
      LaunchedEffect(seriesKey,selectedSeason) {
       if(selectedSeason>0) episodeCount=prefs.getInt("episodes::$seriesKey::$selectedSeason",McuCatalog.find(seriesKey)?.seasons?.getOrNull(selectedSeason-1) ?: 6).coerceAtLeast(1)
      }
      BackHandler {
       when { selectedEpisode!=0 -> selectedEpisode=0;selectedSeason>0 -> selectedSeason=0;else -> selected=null }
      }
      TextButton(onClick={
       when { selectedEpisode!=0 -> selectedEpisode=0;selectedSeason>0 -> selectedSeason=0;else -> selected=null }
      }) { Text("← Zurück") }
      Text(selected!!,fontSize=24.sp,color=Color.White,fontWeight=FontWeight.Bold)
      val revision=refresh
      if(isSeries && selectedSeason==0) {
       Text("Staffeln",color=gold,fontSize=18.sp)
       LazyColumn(Modifier.weight(1f)) {
        items((1..seasonCount).toList()) { number ->
         Card(onClick={selectedSeason=number;selectedEpisode=0},modifier=Modifier.fillMaxWidth().padding(vertical=5.dp)) {
          Column(Modifier.padding(18.dp)) {
           Text("Staffel $number")
           seasonScore(prefs,seriesKey,number)?.let { Text("$it% · ${grade(it)}",color=gold) }
          }
         }
        }
       }
       Button(onClick={seasonCount++;prefs.edit().putInt("seasons::$seriesKey",seasonCount).apply();refresh++},modifier=Modifier.fillMaxWidth()) { Text("Staffel hinzufügen") }
      } else if(isSeries && selectedEpisode==0) {
       Text("Staffel $selectedSeason",color=gold,fontSize=18.sp)
       Row {
        Button(onClick={selectedEpisode=-1}) { Text("Staffel bewerten") }
       }
       val manual=savedPercent(prefs,"$seriesKey::season:$selectedSeason",seasonCategories)
       val average=seasonAverage(prefs,seriesKey,selectedSeason)
       if(manual!=null && average!=null) {
        Text("Aktive Staffelwertung",color=gold)
        val mode=prefs.getString("mode::$seriesKey::$selectedSeason","manual") ?: "manual"
        Row {
         FilterChip(selected=mode=="manual",onClick={
          prefs.edit().putString("mode::$seriesKey::$selectedSeason","manual").apply();refresh++
         },label={Text("Manuell: $manual%")})
         Spacer(Modifier.width(8.dp))
         FilterChip(selected=mode=="episodes",onClick={
          prefs.edit().putString("mode::$seriesKey::$selectedSeason","episodes").apply();refresh++
         },label={Text("Episoden: $average%")})
        }
       } else if(average!=null) Text("Episodendurchschnitt: $average%",color=gold)
       Text("Episoden",color=gold)
       LazyColumn(Modifier.weight(1f)) {
        items((1..episodeCount).toList()) { number ->
         Card(onClick={selectedEpisode=number},modifier=Modifier.fillMaxWidth().padding(vertical=4.dp)) {
          Column(Modifier.padding(16.dp)) {
           Text("Episode $number")
           savedPercent(prefs,"$seriesKey::season:$selectedSeason:episode:$number",episodeCategories)?.let {
            Text("$it% · ${grade(it)}",color=gold)
           }
          }
         }
        }
       }
       Button(onClick={episodeCount++;prefs.edit().putInt("episodes::$seriesKey::$selectedSeason",episodeCount).apply();refresh++},modifier=Modifier.fillMaxWidth()) { Text("Episode hinzufügen") }
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
       if(percent!=null) {
        val report=RatingExport.Report(
         selected!!,
         if(!isSeries) "Film" else if(selectedEpisode == -1) "Staffel $selectedSeason" else "Staffel $selectedSeason · Episode $selectedEpisode",
         "$percent% · ${grade(percent)}",
         criteria.map { it to "${ratings[it] ?: 0}/10" }
        )
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
         OutlinedButton(onClick={exportReport=report;pngLauncher.launch("Scorium-Bewertung.png")}) { Text("Als PNG speichern") }
         OutlinedButton(onClick={exportReport=report;pdfLauncher.launch("Scorium-Bewertung.pdf")}) { Text("Als PDF speichern") }
        }
       }
       LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp)) {
        items(criteria) { criterion ->
         ScoriumRatingCard(criterion,ratings[criterion]) { newScore ->
          ratings[criterion]=newScore
          val json=JSONObject();ratings.forEach { (name,score)->json.put(name,score) }
          prefs.edit().putString(key,json.toString()).apply();refresh++
         }
       }
      }
     }
    } }
   }
  }
 }
}
}
