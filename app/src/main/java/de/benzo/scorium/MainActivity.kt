package de.benzo.scorium

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.delay
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
private val releaseOrder = listOf(
"Iron Man","Der unglaubliche Hulk","Iron Man 2","Thor","Captain America: The First Avenger","Marvel's The Avengers",
"Iron Man 3","MARVEL's Agents of Shield","Thor: The Dark Kingdom","Captain America: The Winter Soldier","Guardians of the Galaxy",
"MARVEL's Daredevil","Avengers: Age of Ultron","Ant-Man","MARVEL's Jessica Jones","Captain America: Civil War","MARVEL's Luke Cage",
"Doctor Strange","MARVEL's Iron Fist","Guardians of the Galaxy Vol. 2","Spider-Man: Homecoming","MARVEL's DEFENDERS",
"Thor: Ragnarok","MARVEL's Punisher","Black Panther","Avengers: Infinity War","Ant-Man and the Wasp","Captain Marvel",
"Avengers: Endgame","Spider-Man: Far From Home","WandaVision","The Falcon and the Winter Soldier","Loki","Black Widow",
"What If...?","Shang-Chi and the Legend of the Ten Rings","Eternals","Hawkeye","Spider-Man: No Way Home",
"Moon Knight","Doctor Strange in the Multiverse of Madness","Ms. Marvel","Thor: Love and Thunder","I Am Groot",
"She-Hulk","Black Panther: Wakanda Forever","Ant-Man and the Wasp: Quantumania","Guardians of the Galaxy Vol. 3",
"Secret Invasion","The Marvels","Echo","X-MEN 97","Deadpool & Wolverine","Agatha All Along",
"Your Friendly Neighborhood Spider-Man","Captain America: Brave New World","Daredevil: Born Again","Thunderbolts*",
"Ironheart","The Fantastic Four: First Steps","Eyes of Wakanda","Marvel Zombies","Wonder Man","VisionQuest",
"Spider-Man: Brand New Day"
)
private fun releaseRank(name:String):Int {
 val rank=releaseOrder.indexOf(name)
 return if(rank>=0) rank else Int.MAX_VALUE
}
private val films get() = McuCatalog.films
private val series get() = (McuCatalog.shows + McuCatalog.importedShows).map { it.title }
private fun recordRecentlyOpened(prefs:android.content.SharedPreferences,name:String) {
 val old=runCatching { JSONArray(prefs.getString("recent_titles","[]")) }.getOrDefault(JSONArray())
 val titles=mutableListOf(name)
 for(i in 0 until old.length()) {
  val title=old.optString(i)
  if(title.isNotBlank() && title!=name && titles.size<12) titles.add(title)
 }
 val result=JSONArray()
 titles.forEach { result.put(it) }
 prefs.edit().putString("recent_titles",result.toString()).apply()
}
class MainActivity: ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  ScoriumBackup.initializeStarterCatalog(this)
  val prefs=getSharedPreferences("ratings",Context.MODE_PRIVATE)
  setContent {
   var showIntro by rememberSaveable { mutableStateOf(true) }
   var selected by remember { mutableStateOf<String?>(null) }
   var home by remember { mutableStateOf(true) }
   var openingMcu by remember { mutableStateOf(false) }
   var mcuHub by remember { mutableStateOf(false) }
   var mcuCollectionRoute by remember { mutableStateOf(false) }
   var mcuPhase by remember { mutableStateOf("Komplett") }
   var mcuTileBounds by remember { mutableStateOf<Rect?>(null) }
   var mcuPosterBounds by remember { mutableStateOf<Rect?>(null) }
   var section by remember { mutableStateOf("Franchises") }
   var showExitDialog by remember { mutableStateOf(false) }
   var selectedSeason by remember { mutableIntStateOf(0) }
   var selectedEpisode by remember { mutableIntStateOf(0) }
   var seasonCount by remember { mutableIntStateOf(1) }
   var episodeCount by remember { mutableIntStateOf(6) }
   var search by remember { mutableStateOf("") }
   var tab by remember { mutableStateOf("Alle") }
   val titleListState=rememberLazyListState()
   var refresh by remember { mutableIntStateOf(0) }
   val extras=remember(refresh) {
    mutableStateListOf<String>().apply {
     val data=runCatching { JSONArray(prefs.getString("custom_titles","[]")) }.getOrDefault(JSONArray())
     for(i in 0 until data.length()) add(data.optString(i))
    }
   }
   var add by remember { mutableStateOf(false) }
   var pendingDelete by remember { mutableStateOf<String?>(null) }
   var pendingReset by remember { mutableStateOf<String?>(null) }
   var editingOrder by remember { mutableStateOf(false) }
   var orderRevision by remember { mutableIntStateOf(0) }
   var newTitle by remember { mutableStateOf("") }
   var newKind by remember { mutableStateOf("Film") }
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
   var backupMessage by remember { mutableStateOf<String?>(null) }
   val backupExport=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
    if(uri!=null) runCatching { ScoriumTransfer.full(this@MainActivity,uri) }
     .onSuccess { parts -> backupMessage="$parts ZIP-Datei(en) mit maximal 24 MB erstellt. Hinweis: Mehrteilige Exporte sind für den GitHub-Upload gedacht; der bisherige Import kann sie noch nicht zusammenführen." }
     .onFailure { backupMessage=it.localizedMessage ?: "Export fehlgeschlagen" }
   }
   val updateExport=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
    if(uri!=null) runCatching { ScoriumTransfer.delta(this@MainActivity,uri) }
     .onSuccess { backupMessage=it+". Hinweis: Der bisherige Import kann Update-Dateien noch nicht zusammenführen." }
     .onFailure { backupMessage=it.localizedMessage ?: "Update-Export fehlgeschlagen" }
   }
   val backupImport=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
    if(uri!=null) runCatching { contentResolver.openInputStream(uri)?.use { ScoriumBackup.restore(this@MainActivity,it) } ?: error("Datei konnte nicht gelesen werden") }
     .onSuccess { posterCount -> backupMessage="Sicherung importiert: $posterCount Poster wiederhergestellt. Filme und Serien wurden aktualisiert.";refresh++ }
     .onFailure { backupMessage=it.localizedMessage ?: "Import fehlgeschlagen" }
   }
   val customSeries=remember(refresh) {
    mutableStateListOf<String>().apply {
     val data=runCatching { JSONArray(prefs.getString("custom_series","[]")) }.getOrDefault(JSONArray())
     for(i in 0 until data.length()) add(data.optString(i))
    }
   }
   val palette=darkColorScheme(primary=gold,background=navy,surface=Color(0xFF182235),onSurface=Color.White,onBackground=Color.White,onPrimary=navy,onSurfaceVariant=Color(0xFFB6C4D5))
   BackHandler(enabled=true) {
    when {
     openingMcu -> openingMcu=false
     showIntro -> showExitDialog=true
     selected!=null && selectedEpisode!=0 -> selectedEpisode=0
     selected!=null && selectedSeason>0 -> selectedSeason=0
     selected!=null -> selected=null
     mcuHub -> { mcuHub=false; mcuCollectionRoute=false; home=true }
     !home -> { if(mcuCollectionRoute) { mcuHub=true; mcuPhase="Komplett" } else home=true }
     else -> showExitDialog=true
    }
   }
   val activeMcuTheme = mcuCollectionRoute && !home && !openingMcu
   val scopedPalette = if(activeMcuTheme) darkColorScheme(primary=Color(0xFFEF3945),background=Color(0xFF150306),surface=Color(0xFF30090F),onSurface=Color.White,onBackground=Color.White,onPrimary=Color.White,onSurfaceVariant=Color(0xFFFFC3C7)) else palette
   CompositionLocalProvider(LocalMcuCollectionTheme provides activeMcuTheme) {
   MaterialTheme(colorScheme=scopedPalette) {
    if(showExitDialog) AlertDialog(
     onDismissRequest={showExitDialog=false},
     title={Text("SCORIUM beenden?")},
     text={Text("Möchtest du die App wirklich schließen?")},
     confirmButton={TextButton(onClick={showExitDialog=false;this@MainActivity.finish()}) { Text("Ja, beenden") }},
     dismissButton={TextButton(onClick={showExitDialog=false}) { Text("Nein, bleiben") }}
    )
    if(backupMessage!=null) AlertDialog(onDismissRequest={backupMessage=null},title={Text("SCORIUM-Sicherung")},text={Text(backupMessage ?: "")},confirmButton={TextButton(onClick={backupMessage=null}){Text("OK")}})
    if(exportError!=null) AlertDialog(onDismissRequest={exportError=null},title={Text("Exportfehler")},text={Text(exportError ?: "")},confirmButton={TextButton(onClick={exportError=null}){Text("OK")}})
    ScoriumBackdrop { Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().then(if(selected!=null) Modifier.verticalScroll(rememberScrollState()) else Modifier).padding(horizontal=18.dp)) {
     if(mcuHub) {
      Spacer(Modifier.fillMaxSize())
     } else if(home) {
      ScoriumHeader()
      Spacer(Modifier.height(12.dp))
      val imageRevision=refresh
      val homeScroll=rememberScrollState()
      Column(Modifier.weight(1f).verticalScroll(homeScroll)) {
       Text("Dein Universum",fontSize=29.sp,fontWeight=FontWeight.Bold,color=Color.White)
       Spacer(Modifier.height(14.dp))
       Card(onClick={mcuCollectionRoute=false;home=false;section="Franchises";tab="Alle";search=""},modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(16.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF1A233D))) { Text("⌕  Filme und Serien suchen …",modifier=Modifier.padding(horizontal=18.dp,vertical=22.dp),color=Color(0xFFB5B7CD),fontSize=16.sp) }
       Spacer(Modifier.height(22.dp))
       Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
        Text("Meine Sammlungen",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Color.White)
        TextButton(onClick={home=false;section="Franchises";tab="Alle"}) { Text("Alle ansehen") }
       }
       Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        Box(Modifier.onGloballyPositioned { mcuTileBounds = it.boundsInWindow() }.graphicsLayer { alpha = if (openingMcu) 0f else 1f }) {
         ScoriumCollectionTile("Marvel / MCU","Filme & Serien","collection::mcu",onClick={mcuCollectionRoute=true;openingMcu=true},onImageSaved={refresh++},revision=refresh,onPosterBounds={ mcuPosterBounds=it })
        }
        ScoriumCollectionTile("Neue Sammlung","Eigene Liste erstellen","collection::new",onClick={home=false;section="Franchises";tab="Alle";add=true},onImageSaved={refresh++},revision=refresh)
        ScoriumCollectionTile("Filme","Alle Filme","collection::films",onClick={mcuCollectionRoute=false;home=false;section="Filme";tab="Filme"},onImageSaved={refresh++},revision=refresh)
        ScoriumCollectionTile("Serien","Alle Serien","collection::series",onClick={mcuCollectionRoute=false;home=false;section="Serien";tab="Serien"},onImageSaved={refresh++},revision=refresh)
       }
       Spacer(Modifier.height(26.dp))
       Text("Zuletzt angesehen",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Color.White)
       Spacer(Modifier.height(12.dp))
       val recent=remember(refresh) {
        val stored=runCatching { JSONArray(prefs.getString("recent_titles","[]")) }.getOrDefault(JSONArray())
        (0 until stored.length()).map { stored.optString(it) }.filter { it.isNotBlank() }
       }
       if(recent.isEmpty()) Text("Hier erscheinen die Filme und Serien, die du öffnest.",color=Color(0xFFB5B7CD))
       Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        recent.forEach { name ->
         Column(Modifier.width(128.dp).clickable { selected=name;selectedSeason=0;selectedEpisode=0;recordRecentlyOpened(prefs,name);refresh++;home=false }) {
          ScoriumPoster(name,Modifier.width(128.dp).height(175.dp),refresh)
          Spacer(Modifier.height(6.dp))
          Text(name,color=Color.White,fontWeight=FontWeight.Bold,maxLines=2)
         }
        }
       }
       Spacer(Modifier.height(24.dp))
       Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick={home=false;section="Ranking"}) { Text("Ranking") }
        OutlinedButton(onClick={home=false;section="Download"}) { Text("Downloads") }
       }
       Spacer(Modifier.height(18.dp))
       Text("Datensicherung",color=Color.White,fontSize=18.sp,fontWeight=FontWeight.Bold)
       Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
         SmallIconAction(Icons.Default.FileDownload,"Komplettsicherung exportieren") { backupExport.launch(null) }
         SmallIconAction(Icons.Default.FileDownload,"Änderungen exportieren") { updateExport.launch(null) }
        }
        SmallIconAction(Icons.Default.FileUpload,"Sicherung importieren") { backupImport.launch(arrayOf("application/zip","application/octet-stream","*/*")) }
        Text("Exportiert werden Katalog und Poster, keine persönlichen Bewertungen.",color=Color.LightGray,fontSize=12.sp)
       }
       Spacer(Modifier.height(28.dp))
      }
     } else if(selected==null) {
      TextButton(onClick={if(mcuCollectionRoute) { mcuHub=true;mcuPhase="Komplett" } else home=true}) { Text(if(mcuCollectionRoute) "← MCU-Phasen" else "← Startseite") }
      if(section=="Ranking" || section=="Download") {
       val rated=(films+extras+series+customSeries).distinct().mapNotNull { name ->
        val value=if(name in series || name in customSeries) {
         val count=prefs.getInt("seasons::$name",McuCatalog.find(name)?.seasons?.size ?: 1)
         val scores=(1..count).mapNotNull { seasonScore(prefs,name,it) }
         if(scores.size==count) kotlin.math.round(scores.average()).toInt() else null
        } else savedPercent(prefs,name,categories)
        value?.let { name to it }
       }.sortedByDescending { it.second }
       val revision=refresh
       Text(if(section=="Ranking") "DEIN RANKING" else "DEINE EXPORTS",color=gold,fontSize=20.sp,fontWeight=FontWeight.Bold)
       Spacer(Modifier.height(12.dp))
       if(rated.isEmpty()) Text("Noch keine vollständig bewerteten Titel vorhanden.",color=Color.White)
       LazyColumn(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        items(rated) { (name,score) ->
         Column {
          ScoriumTitleCard(name,"${score / 10}.${score % 10} · ${grade(score)}",score,grade(score),onClick={
           selected=name;selectedSeason=0;selectedEpisode=0;recordRecentlyOpened(prefs,name);refresh++
          })
          if(section=="Download" && name !in series && name !in customSeries) {
           val json=runCatching { JSONObject(prefs.getString(name,"{}") ?: "{}") }.getOrDefault(JSONObject())
           val report=RatingExport.Report(name,"Film","${score / 10}.${score % 10} · ${grade(score)}",categories.map { it to "${json.optInt(it)}/10" })
           Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick={exportReport=report;pngLauncher.launch("Scorium-$name.png")}) { Text("PNG") }
            OutlinedButton(onClick={exportReport=report;pdfLauncher.launch("Scorium-$name.pdf")}) { Text("PDF") }
           }
          }
         }
        }
       }
      } else {
      if(mcuCollectionRoute) {
       Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally) {
        Image(painter=painterResource(R.drawable.scorium_mcu_logo),contentDescription="MARVEL STUDIOS MCU",modifier=Modifier.fillMaxWidth().height(92.dp),contentScale=ContentScale.Fit)
        Text(mcuPhase.uppercase(),color=Color(0xFFC8CDD3),fontSize=20.sp,fontWeight=FontWeight.Bold,letterSpacing=2.sp,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
       }
      }
      OutlinedTextField(search,{search=it},label={Text("Suchen")},modifier=Modifier.fillMaxWidth())
      Row { listOf("Alle","Filme","Serien").forEach { name -> FilterChip(selected=tab==name,onClick={tab=name},label={Text(name)});Spacer(Modifier.width(6.dp)) } }
      val entries=((if(tab=="Serien") emptyList() else films+extras)+(if(tab=="Filme") emptyList() else (series+customSeries).distinct())).distinct().filter { it.contains(search,true) && (!mcuCollectionRoute || McuCatalog.inPhase(it,mcuPhase)) }.sortedWith(compareBy<String> {
       if(mcuCollectionRoute) releaseRank(it) else {
        val saved=prefs.getString("order::titles","")?.split("|") ?: emptyList()
        val pos=saved.indexOf(it)
        if(pos>=0) pos else 100000+releaseRank(it)
       }
      }.thenBy { it })
      val revision=refresh
      if(!mcuCollectionRoute) Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End) {
       SmallIconAction(Icons.Default.Edit,"Reihenfolge bearbeiten") { editingOrder=!editingOrder }
       SmallIconAction(Icons.Default.Add,"Titel hinzufügen") { add=true }
      }
      LazyColumn(Modifier.weight(1f),state=titleListState) {
       items(entries,key={ it }) { name ->
        val isShow=name in series || name in customSeries
        val value=if(isShow) {
         val count=prefs.getInt("seasons::$name",McuCatalog.find(name)?.seasons?.size ?: 1)
         val scores=(1..count).mapNotNull { seasonScore(prefs,name,it) }
         if(scores.size==count) kotlin.math.round(scores.average()).toInt() else null
        } else savedPercent(prefs,name,categories)
        Row(verticalAlignment=Alignment.CenterVertically) {
         Box(Modifier.weight(1f)) {
          ScoriumTitleCard(name,if(isShow) "Serie" else "Film",value,value?.let { grade(it) },onClick={
           selected=name;selectedSeason=0;selectedEpisode=0;recordRecentlyOpened(prefs,name);refresh++
          },revision=refresh)
         }
         if(editingOrder && !mcuCollectionRoute) Column {
          SmallIconAction(Icons.Default.ArrowUpward,"Nach oben") {
           val order=entries.toMutableList();val index=order.indexOf(name)
           if(index>0) { java.util.Collections.swap(order,index,index-1);prefs.edit().putString("order::titles",order.joinToString("|")).apply();orderRevision++;refresh++ }
          }
          SmallIconAction(Icons.Default.ArrowDownward,"Nach unten") {
           val order=entries.toMutableList();val index=order.indexOf(name)
           if(index>=0 && index<order.lastIndex) { java.util.Collections.swap(order,index,index+1);prefs.edit().putString("order::titles",order.joinToString("|")).apply();orderRevision++;refresh++ }
          }
         }
        }
       }
      }

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
      }
     } else {
      val isSeries=selected!! in series || selected!! in customSeries
      val seriesKey=selected!!
      LaunchedEffect(seriesKey) {
       seasonCount=prefs.getInt("seasons::$seriesKey",McuCatalog.find(seriesKey)?.seasons?.size ?: 1).coerceAtLeast(1)
       episodeCount=prefs.getInt("episodes::$seriesKey::1",McuCatalog.find(seriesKey)?.available?.firstOrNull() ?: 6).coerceAtLeast(1)
      }
      LaunchedEffect(seriesKey,selectedSeason) {
       if(selectedSeason>0) episodeCount=prefs.getInt("episodes::$seriesKey::$selectedSeason",McuCatalog.find(seriesKey)?.available?.getOrNull(selectedSeason-1) ?: 6).coerceAtLeast(1)
      }
      TextButton(onClick={
       when { selectedEpisode!=0 -> selectedEpisode=0;selectedSeason>0 -> selectedSeason=0;else -> selected=null }
      }) { Text("← Zurück") }
      ScoriumDetailHero(selected!!,if(isSeries) "Serie" else "Film",refresh,
       if(isSeries) {
        val count=prefs.getInt("seasons::${selected!!}",McuCatalog.find(selected!!)?.seasons?.size ?: 1)
        val scores=(1..count).mapNotNull { seasonScore(prefs,selected!!,it) }
        if(scores.size==count) kotlin.math.round(scores.average()).toInt() else null
       } else savedPercent(prefs,selected!!,categories))
      Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
       PosterPicker(selected!!) { refresh++ }
       SmallIconAction(Icons.Default.Refresh,"Bewertung zurücksetzen") { pendingReset=selected }
       if(selected!! in extras || selected!! in customSeries) SmallIconAction(Icons.Default.Delete,"Eigenen Titel löschen") { pendingDelete=selected }
      }
      if(isSeries && selectedSeason>0) {
       Spacer(Modifier.height(8.dp))
       Text("Staffel $selectedSeason · Eigenes Bild",color=gold)
       ScoriumPoster("$seriesKey::season:$selectedSeason",Modifier.width(112.dp).height(154.dp),refresh)
       PosterPicker("$seriesKey::season:$selectedSeason") { refresh++ }
       if(selectedEpisode>0) {
        Text("Episode $selectedEpisode · Staffelposter",color=gold)
        ScoriumPoster("$seriesKey::season:$selectedSeason",Modifier.width(112.dp).height(154.dp),refresh)
        PosterPicker("$seriesKey::season:$selectedSeason:episode:$selectedEpisode") { refresh++ }
       }
      }
      Spacer(Modifier.height(8.dp))
      val revision=refresh
      if(isSeries && selectedSeason==0) {
       Text("Staffeln",color=gold,fontSize=18.sp)
       Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
        (1..seasonCount).forEach { number ->
         val score=seasonScore(prefs,seriesKey,number)
         ScoriumTitleCard("Staffel $number","Staffel",score,score?.let { grade(it) },onClick={
          selectedSeason=number;selectedEpisode=0
         },imageKey="$seriesKey::season:$number",revision=refresh)
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
         },label={Text("Manuell: ${manual / 10}.${manual % 10}")})
         Spacer(Modifier.width(8.dp))
         FilterChip(selected=mode=="episodes",onClick={
          prefs.edit().putString("mode::$seriesKey::$selectedSeason","episodes").apply();refresh++
         },label={Text("Episoden: ${average / 10}.${average % 10}")})
        }
       } else if(average!=null) Text("Episodendurchschnitt: ${average / 10}.${average % 10}",color=gold)
       Text("Episoden",color=gold)
       Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
        (1..episodeCount).forEach { number ->
         val score=savedPercent(prefs,"$seriesKey::season:$selectedSeason:episode:$number",episodeCategories)
         ScoriumTitleCard(EpisodeTitles.title(seriesKey,selectedSeason,number)?.let { "$number · $it" } ?: "Episode $number","Episode",score,score?.let { grade(it) },onClick={
          selectedEpisode=number
         },imageKey="$seriesKey::season:$selectedSeason",revision=refresh)
        }
       }
       Button(onClick={episodeCount++;prefs.edit().putInt("episodes::$seriesKey::$selectedSeason",episodeCount).apply();refresh++},modifier=Modifier.fillMaxWidth()) { Text("Episode hinzufügen") }
      } else {
       val key=selected!! + if(!isSeries) "" else if(selectedEpisode == -1) "::season:$selectedSeason" else "::season:$selectedSeason:episode:$selectedEpisode"
       val criteria=if(!isSeries) categories else if(selectedEpisode == -1) seasonCategories else episodeCategories
       if(isSeries) Text(if(selectedEpisode == -1) "Staffel $selectedSeason bewerten" else "Episode $selectedEpisode bewerten",color=gold)
       val ratings=remember(key,refresh) {
        val json=runCatching { JSONObject(prefs.getString(key,"{}") ?: "{}") }.getOrDefault(JSONObject())
        mutableStateMapOf<String,Int>().apply { criteria.forEach { if(json.has(it)) put(it,json.optInt(it)) } }
       }
       val percent=if(criteria.all { ratings.containsKey(it) }) criteria.sumOf { ratings[it] ?: 0 } * 10 / criteria.size else null
       if(percent==null) Text("Bewertet: ${ratings.size}/${criteria.size}",color=gold,fontSize=19.sp)
       else Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        ScoriumScoreRing(percent,100.dp)
        Text("GESAMTBEWERTUNG",color=Color(0xFFC5C7D1),fontSize=13.sp,fontWeight=FontWeight.Bold)
       }
       if(percent!=null) {
        val report=RatingExport.Report(
         selected!!,
         if(!isSeries) "Film" else if(selectedEpisode == -1) "Staffel $selectedSeason" else "Staffel $selectedSeason · Episode $selectedEpisode",
         "${percent / 10}.${percent % 10} · ${grade(percent)}",
         criteria.map { it to "${ratings[it] ?: 0}/10" }
        )
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
         SmallIconAction(Icons.Default.Image,"Bewertung als PNG exportieren") { exportReport=report;pngLauncher.launch("Scorium-Bewertung.png") }
         SmallIconAction(Icons.Default.PictureAsPdf,"Bewertung als PDF exportieren") { exportReport=report;pdfLauncher.launch("Scorium-Bewertung.pdf") }
        }
       }
       Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
        criteria.forEach { criterion ->
         ScoriumRatingCard(criterion,ratings[criterion]) { newScore ->
          ratings[criterion]=newScore
          val json=JSONObject();ratings.forEach { (name,score)->json.put(name,score) }
          prefs.edit().putString(key,json.toString()).apply();refresh++
         }
       }
      }
     }
    } }
    // The existing home UI is composed underneath for a seamless fade-out.
    // Crossfade the MCU phase hub in and out over the destination content.
    // Keep the hub as a separate layer so the selected phase list is visible underneath.
    Crossfade(targetState=mcuHub,animationSpec=tween(durationMillis=450),label="mcuPhaseHubCrossfade") { visible ->
     if(visible) McuPhaseHub(revision=refresh,onImageSaved={refresh++},onBack={mcuHub=false;mcuCollectionRoute=false;home=true},onPhase={phase -> mcuCollectionRoute=true;mcuPhase=phase;mcuHub=false;home=false;section="Franchises";tab="Alle";search=""})
    }
    if(openingMcu) McuOpeningOverlay(tileBounds=mcuTileBounds,posterBounds=mcuPosterBounds,imageRevision=refresh,onComplete={openingMcu=false},onReveal={home=false;mcuCollectionRoute=true;mcuHub=true;mcuPhase="Komplett"},onSkip={openingMcu=false;home=false;mcuCollectionRoute=true;mcuHub=true;mcuPhase="Komplett"})
    pendingReset?.let { target ->
     AlertDialog(onDismissRequest={pendingReset=null},title={Text("Bewertung zurücksetzen?")},
      text={Text("Alle Bewertungen für „$target“ werden gelöscht. Der Titel und sein Poster bleiben erhalten.")},
      confirmButton={TextButton(onClick={
       val editor=prefs.edit()
       prefs.all.keys.filter { it==target || it.startsWith("$target::season:") }.forEach { editor.remove(it) }
       editor.apply();pendingReset=null;refresh++
      }) { Text("Zurücksetzen") }},
      dismissButton={TextButton(onClick={pendingReset=null}) { Text("Abbrechen") }})
    }
    pendingDelete?.let { target ->
     AlertDialog(onDismissRequest={pendingDelete=null},title={Text("Titel löschen?")},
      text={Text("„$target“ und seine gespeicherten Bewertungen werden gelöscht. Das lässt sich nicht rückgängig machen.")},
      confirmButton={TextButton(onClick={
       val key=if(target in customSeries) "custom_series" else "custom_titles"
       val current=runCatching { JSONArray(prefs.getString(key,"[]")) }.getOrDefault(JSONArray())
       val result=JSONArray()
       for(i in 0 until current.length()) if(current.optString(i)!=target) result.put(current.optString(i))
       val editor=prefs.edit().putString(key,result.toString())
       prefs.all.keys.filter { it==target || it.startsWith("$target::") || it.startsWith("seasons::$target") || it.startsWith("episodes::$target") }.forEach { editor.remove(it) }
       editor.apply();selected=null;pendingDelete=null;refresh++
      }) { Text("Löschen") }},
      dismissButton={TextButton(onClick={pendingDelete=null}) { Text("Abbrechen") }})
    }
    if(showIntro) ScoriumIntroScreen(onEnter={showIntro=false})
   }
   }
  }
 }
}
}
