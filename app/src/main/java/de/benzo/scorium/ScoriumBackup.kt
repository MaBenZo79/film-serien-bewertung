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
 fun export(context:Context,out:OutputStream) {
  val json=JSONObject()
  context.getSharedPreferences("ratings",Context.MODE_PRIVATE).all.forEach { (key,value) ->
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
 fun restore(context:Context,input:InputStream) {
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
  val editor=context.getSharedPreferences("ratings",Context.MODE_PRIVATE).edit().clear()
  data.keys().forEach { key ->
   when(val value=data.get(key)) {
    is String -> editor.putString(key,value)
    is Int -> editor.putInt(key,value)
    is Long -> editor.putLong(key,value)
    is Double -> editor.putFloat(key,value.toFloat())
    is Boolean -> editor.putBoolean(key,value)
   }
  }
  check(editor.commit()) { "Wiederherstellung fehlgeschlagen" }
  val folder=File(context.filesDir,"posters")
  folder.mkdirs()
  posters.forEach { (name,bytes) -> File(folder,name).writeBytes(bytes) }
 }
}
