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

private val gold = Color(0xFFE4C477)
private val navy = Color(0xFF0D111B)
private val categories = listOf("Handlung & Drehbuch","Charaktere & Entwicklung","Antagonist & Konflikt","Schauspiel & Dialoge","Spannung & Erzähltempo","Action & Inszenierung","Visuelle Qualität & Effekte","Musik & Sounddesign","Emotionale Wirkung & Atmosphäre","Langlebigkeit & Zeitlosigkeit")
private val films = listOf("Iron Man","Der unglaubliche Hulk","Iron Man 2","Thor","Captain America: The First Avenger","Marvel's The Avengers","Iron Man 3","Thor: The Dark Kingdom","Captain America: The Winter Soldier","Guardians of the Galaxy","Avengers: Age of Ultron","Ant-Man","Captain America: Civil War","Doctor Strange","Guardians of the Galaxy Vol. 2","Spider-Man: Homecoming","Thor: Ragnarok","Black Panther","Avengers: Infinity War","Ant-Man and the Wasp","Captain Marvel","Avengers: Endgame","Spider-Man: Far From Home")
private val series = listOf("WandaVision","The Falcon and the Winter Soldier","Loki","What If...?","Hawkeye","Moon Knight","Ms. Marvel","She-Hulk","Secret Invasion","Echo","Agatha All Along","Daredevil: Born Again")
private fun grade(p: Int) = when { p>=95->"A+";p>=90->"A";p>=85->"A-";p>=80->"B+";p>=75->"B";p>=70->"B-";p>=65->"C+";p>=60->"C";p>=55->"C-";p>=50->"D+";p>=45->"D";p>=40->"D-";else->"F" }
class MainActivity: ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val prefs=getSharedPreferences("ratings",Context.MODE_PRIVATE)
  setContent {
   var selected by remember { mutableStateOf<String?>(null) }
   var search by remember { mutableStateOf("") }
   var tab by remember { mutableStateOf("Alle") }
   val extras=remember { mutableStateListOf<String>() }
   var add by remember { mutableStateOf(false) }
   var newTitle by remember { mutableStateOf("") }
   val palette=darkColorScheme(primary=gold,background=navy,surface=Color(0xFF1B2534),onSurface=Color.White,onBackground=Color.White)
   MaterialTheme(colorScheme=palette) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
     Text("SCORIUM",fontSize=32.sp,color=gold)
     Text("Dein Film- und Serienarchiv",fontSize=13.sp)
     Spacer(Modifier.height(16.dp))
     if(selected==null) {
      OutlinedTextField(search,{search=it},label={Text("Suchen")},modifier=Modifier.fillMaxWidth())
      Row { listOf("Alle","Filme","Serien").forEach { name -> FilterChip(selected=tab==name,onClick={tab=name},label={Text(name)});Spacer(Modifier.width(6.dp)) } }
      val entries=((if(tab=="Serien") emptyList() else films)+(if(tab=="Filme") emptyList() else series)+extras).filter { it.contains(search,true) }
      LazyColumn(Modifier.weight(1f)) {
       items(entries) { name ->
        Card(onClick={selected=name},modifier=Modifier.fillMaxWidth().padding(vertical=5.dp)) {
         Column(Modifier.padding(16.dp)) {
          Text(name,fontSize=18.sp)
          Text(if(name in series) "Serie" else "Film",color=gold,fontSize=12.sp)
         }
        }
       }
      }
      Button(onClick={add=true},modifier=Modifier.fillMaxWidth()) { Text("Titel hinzufügen") }
      if(add) AlertDialog(onDismissRequest={add=false},title={Text("Neuer Titel")},text={
       OutlinedTextField(newTitle,{newTitle=it},label={Text("Name")})
      },confirmButton={TextButton(onClick={if(newTitle.isNotBlank()){extras.add(newTitle.trim());newTitle=""};add=false}){Text("Hinzufügen")}},
       dismissButton={TextButton(onClick={add=false}){Text("Abbrechen")}})
     } else {
      BackHandler { selected=null }
      TextButton(onClick={selected=null}) { Text("← Zurück") }
      Text(selected!!,fontSize=24.sp)
      val key=selected!!
      val ratings=remember(key) {
       val json=runCatching { JSONObject(prefs.getString(key,"{}") ?: "{}") }.getOrDefault(JSONObject())
       mutableStateMapOf<String,Int>().apply { categories.forEach { if(json.has(it)) put(it,json.optInt(it)) } }
      }
      val percent=if(categories.all { ratings.containsKey(it) }) categories.sumOf { ratings[it] ?: 0 } * 10 / categories.size else null
      Text(if(percent==null) "Bewertet: ${ratings.size}/10" else "Gesamt: ${percent}% · ${grade(percent)}",color=gold,fontSize=19.sp)
      LazyColumn {
       items(categories) { criterion ->
        Card(Modifier.fillMaxWidth().padding(vertical=5.dp)) {
         Column(Modifier.padding(14.dp)) {
          Text(criterion)
          val value=ratings[criterion]
          Text(value?.let { "${it}/10" } ?: "Noch nicht bewertet",color=gold)
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
