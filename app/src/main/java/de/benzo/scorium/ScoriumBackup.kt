package de.benzo.scorium
import android.content.Context
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object ScoriumBackup {
 /**
  * Only portable catalog metadata belongs in shared backups.
  * Ratings use title names (and season/episode suffixes) as preference keys,
  * so do not export arbitrary preference entries from the "ratings" store.
  * Local ratings remain untouched on the exporting device.
  */
 fun isCatalogKey(key: String): Boolean =
  key == "custom_titles" ||
  key == "custom_series" ||
  key.startsWith("seasons::") ||
  key.startsWith("episodes::")

 /**
  * Public starter catalog derived from the user's backup. Ratings and personal history
  * are deliberately excluded. Never overwrites data on subsequent launches.
  * The complete built-in MCU catalog continues to come from McuCatalog.
  */
 fun initializeStarterCatalog(context:Context) {
  val prefs=context.getSharedPreferences("ratings",Context.MODE_PRIVATE)
  if(prefs.getBoolean("starter_assets_v4_initialized",false)) return
  val data=JSONObject(context.assets.open("starter/catalog.json").bufferedReader().use { it.readText() })
  val editor=prefs.edit()
  data.keys().forEach { key ->
   if(!prefs.contains(key)) {
    when(val value=data.get(key)) {
     is String -> editor.putString(key,value)
     is Int -> editor.putInt(key,value)
     is Long -> editor.putLong(key,value)
     is Boolean -> editor.putBoolean(key,value)
    }
   }
  }
  // Starter posters stay bundled inside the APK and are read on demand.
  // Do NOT duplicate 273 JPEGs into app-private storage on a fresh install.
  // Existing files from older versions (including edited posters) are never
  // touched; the UI always prefers those local overrides over bundled images.
  editor.putBoolean("starter_catalog_initialized",true)
  editor.putBoolean("starter_assets_v2_initialized",true)
  editor.putBoolean("starter_assets_v3_initialized",true)
  editor.putBoolean("starter_assets_v4_initialized",true)
  check(editor.commit()) { "Startkatalog konnte nicht gespeichert werden" }
 }

 fun export(context:Context,out:OutputStream) {
  val json=JSONObject()
  context.getSharedPreferences("ratings",Context.MODE_PRIVATE).all.forEach { (key,value) ->
   if (!isCatalogKey(key)) return@forEach
   when(value) {
    is String -> json.put(key,value)
    is Int -> json.put(key,value)
    is Long -> json.put(key,value)
    is Boolean -> json.put(key,value)
    is Float -> json.put(key,value.toDouble())
   }
  }
  ZipOutputStream(out).use { zip ->
   zip.putNextEntry(ZipEntry("scorium-data.json"))
   zip.write(json.toString().toByteArray(Charsets.UTF_8))
   zip.closeEntry()
   File(context.filesDir,"posters").listFiles()?.filter { it.isFile && it.extension=="jpg" }?.forEach { file ->
    zip.putNextEntry(ZipEntry("posters/"+file.name))
    file.inputStream().use { it.copyTo(zip) }
    zip.closeEntry()
   }
  }
 }
 fun restore(context:Context,input:InputStream): Int {
  var json:JSONObject?=null
  val posters=mutableMapOf<String,ByteArray>()
  var total=0L
  ZipInputStream(input).use { zip ->
   while(true) {
    val entry=zip.nextEntry ?: break
    if(!entry.isDirectory && (entry.name=="scorium-data.json" || Regex("posters/[0-9a-f]{24}[.]jpg").matches(entry.name))) {
     val output=java.io.ByteArrayOutputStream()
     val buffer=ByteArray(8192)
     while(true) {
      val n=zip.read(buffer)
      if(n<0) break
      total+=n
      require(total<=150L*1024*1024) { "Sicherung zu groß" }
      output.write(buffer,0,n)
     }
     if(entry.name=="scorium-data.json") json=JSONObject(output.toString("UTF-8"))
     else posters[entry.name.substringAfter('/')]=output.toByteArray()
    }
    zip.closeEntry()
   }
  }
  val data=json ?: error("Keine gültige SCORIUM-Sicherung")
  // Validate the complete archive before changing any existing ratings or posters.
  require(posters.size <= 5000) { "Zu viele Poster in der Sicherung" }
  posters.forEach { (name, bytes) ->
   require(bytes.size <= 20 * 1024 * 1024) { "Poster zu groß: $name" }
   require(bytes.size >= 3 &&
    (bytes[0].toInt() and 255) == 0xFF &&
    (bytes[1].toInt() and 255) == 0xD8 &&
    (bytes[2].toInt() and 255) == 0xFF
   ) { "Ungültige Posterdatei: $name" }
  }
  // Merge the portable catalog with existing local data. Never clear ratings,
  // collection preferences, or the starter-catalog initialization marker.
  val editor=context.getSharedPreferences("ratings",Context.MODE_PRIVATE).edit()
  data.keys().forEach { key ->
   if (!isCatalogKey(key)) return@forEach
   when(val value=data.get(key)) {
    is String -> editor.putString(key,value)
    is Int -> editor.putInt(key,value)
    is Long -> editor.putLong(key,value)
    is Double -> editor.putFloat(key,value.toFloat())
    is Boolean -> editor.putBoolean(key,value)
   }
  }
  val folder=File(context.filesDir,"posters")
  folder.mkdirs()
  posters.forEach { (name,bytes) ->
   val target=File(folder,name)
   val temp=File(folder,"$name.restore")
   temp.writeBytes(bytes)
   if(!temp.renameTo(target)) { temp.copyTo(target,overwrite=true);temp.delete() }
  }
  check(editor.commit()) { "Wiederherstellung fehlgeschlagen" }
  return posters.size
 }
 /**
  * Portable PERSONAL ratings only. No posters, catalog, collection layout,
  * recently viewed titles or starter flags.
  */
 private fun isRatingKey(key:String):Boolean =
  key.startsWith("mode::") ||
  (key.isNotBlank() && !isCatalogKey(key) &&
   !key.startsWith("starter_") &&
   !key.startsWith("poster") &&
   !key.startsWith("collection") &&
   !key.startsWith("order::") &&
   key !in setOf("recent_titles","user_collections"))

 fun exportRatings(context:Context,out:OutputStream):Int {
  val ratings=JSONObject()
  context.getSharedPreferences("ratings",Context.MODE_PRIVATE).all.forEach { (key,value) ->
   if (!isRatingKey(key) || value !is String) return@forEach
   if (key.startsWith("mode::")) {
    if (value in setOf("auto","manual","episodes")) ratings.put(key,value)
   } else {
    val parsed=runCatching { JSONObject(value) }.getOrNull() ?: return@forEach
    if (parsed.length()>0 && parsed.keys().asSequence().all {
      parsed.optInt(it,-1) in 1..10
     }) ratings.put(key,parsed)
   }
  }
  val count=ratings.length()
  val envelope=JSONObject().put("format","scorium-ratings-v1").put("ratings",ratings)
  ZipOutputStream(out).use { zip ->
   zip.putNextEntry(ZipEntry("scorium-ratings.json"))
   zip.write(envelope.toString().toByteArray(Charsets.UTF_8))
   zip.closeEntry()
  }
  return count
 }

 fun importRatings(context:Context,input:InputStream):Int {
  var content:String?=null
  var total=0
  ZipInputStream(input).use { zip ->
   while (true) {
    val entry=zip.nextEntry ?: break
    if (!entry.isDirectory && entry.name=="scorium-ratings.json") {
     require(content==null) { "Doppelte Bewertungsdatei" }
     val output=java.io.ByteArrayOutputStream()
     val buffer=ByteArray(8192)
     while (true) {
      val n=zip.read(buffer)
      if (n<0) break
      total+=n
      require(total<=5*1024*1024) { "Bewertungssicherung zu groß" }
      output.write(buffer,0,n)
     }
     content=output.toString("UTF-8")
    }
    zip.closeEntry()
   }
  }
  val root=JSONObject(content ?: error("Keine Bewertungssicherung. Bitte eine neue SCORIUM-Bewertungssicherung auswählen."))
  require(root.optString("format")=="scorium-ratings-v1") { "Unbekanntes Sicherungsformat" }
  val ratings=root.getJSONObject("ratings")
  require(ratings.length()<=20000) { "Zu viele Bewertungen" }
  val values=mutableMapOf<String,String>()
  ratings.keys().forEach { key ->
   require(isRatingKey(key)) { "Ungültiger Bewertungsschlüssel" }
   if (key.startsWith("mode::")) {
    val mode=ratings.getString(key)
    require(mode in setOf("auto","manual","episodes")) { "Ungültiger Staffelmodus" }
    values[key]=mode
   } else {
    val item=ratings.getJSONObject(key)
    require(item.length() in 1..30 && item.keys().asSequence().all {
      item.optInt(it,-1) in 1..10
    }) { "Ungültige Bewertung" }
    values[key]=item.toString()
   }
  }
  val editor=context.getSharedPreferences("ratings",Context.MODE_PRIVATE).edit()
  values.forEach { (key,value) -> editor.putString(key,value) }
  check(editor.commit()) { "Bewertungen konnten nicht gespeichert werden" }
  return values.size
 }

}
