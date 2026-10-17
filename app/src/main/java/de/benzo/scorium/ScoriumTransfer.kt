package de.benzo.scorium

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.OutputStream
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Export-only archives. The existing SCORIUM restore function is deliberately unchanged.
 * Multi-part archives and delta archives are for transfer/catalog updates; they cannot
 * currently be restored as a complete set using the single-file restore button.
 */
object ScoriumTransfer {
 private const val MAX_BYTES = 24L * 1000L * 1000L
 private const val TARGET_BYTES = 23L * 1000L * 1000L
 private const val SNAPSHOT = "scorium_export_snapshot_v1"

 private fun fingerprint(bytes: ByteArray): String =
  MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

 private fun state(context: Context): Pair<JSONObject, Map<String, File>> {
  val data = JSONObject()
  context.getSharedPreferences("ratings", Context.MODE_PRIVATE).all.forEach { (key, value) ->
   if (!ScoriumBackup.isCatalogKey(key)) return@forEach
   when (value) {
    is String -> data.put(key, value)
    is Int -> data.put(key, value)
    is Long -> data.put(key, value)
    is Boolean -> data.put(key, value)
    is Float -> data.put(key, value.toDouble())
   }
  }
  val posters = File(context.filesDir, "posters").listFiles()
   ?.filter { it.isFile && Regex("[0-9a-f]{24}\\.jpg").matches(it.name) }
   ?.associateBy { it.name } ?: emptyMap()
  return data to posters
 }

 private fun snapshot(data: JSONObject, posters: Map<String, File>): JSONObject {
  val result = JSONObject()
  val keys = JSONObject()
  data.keys().forEach { key -> keys.put(key, fingerprint(data.get(key).toString().toByteArray(Charsets.UTF_8))) }
  val images = JSONObject()
  posters.forEach { (name, file) ->
   val md = MessageDigest.getInstance("SHA-256")
   file.inputStream().use { stream ->
    val buf = ByteArray(65536)
    while (true) { val n = stream.read(buf); if (n < 0) break; md.update(buf, 0, n) }
   }
   images.put(name, md.digest().joinToString("") { "%02x".format(it) })
  }
  result.put("keys", keys)
  result.put("posters", images)
  return result
 }

 private fun saved(context: Context): JSONObject? {
  val text = context.getSharedPreferences(SNAPSHOT, Context.MODE_PRIVATE).getString("last", null)
  return text?.let { runCatching { JSONObject(it) }.getOrNull() }
 }

 private fun save(context: Context, state: JSONObject) {
  check(context.getSharedPreferences(SNAPSHOT, Context.MODE_PRIVATE).edit()
   .putString("last", state.toString()).commit()) { "Exportstand konnte nicht gespeichert werden" }
 }

 private fun zip(out: OutputStream, json: JSONObject?, posters: List<File>, manifest: JSONObject? = null) {
  ZipOutputStream(out).use { archive ->
   if (json != null) {
    archive.putNextEntry(ZipEntry("scorium-data.json"))
    archive.write(json.toString().toByteArray(Charsets.UTF_8))
    archive.closeEntry()
   }
   if (manifest != null) {
    archive.putNextEntry(ZipEntry("scorium-update-manifest.json"))
    archive.write(manifest.toString().toByteArray(Charsets.UTF_8))
    archive.closeEntry()
   }
   posters.forEach { file ->
    archive.putNextEntry(ZipEntry("posters/" + file.name))
    file.inputStream().use { it.copyTo(archive) }
    archive.closeEntry()
   }
  }
 }

 /** Saves one or more files in a user-selected directory; never creates an oversized file. */
 fun full(context: Context, tree: Uri): Int {
  val (data, posters) = state(context)
  val current = snapshot(data, posters)
  val dataBytes = data.toString().toByteArray(Charsets.UTF_8).size.toLong()
  require(dataBytes < TARGET_BYTES) { "Katalogdaten allein überschreiten 23 MB" }
  val groups = mutableListOf<MutableList<File>>()
  var group = mutableListOf<File>()
  var size = dataBytes + 4096L
  posters.values.sortedBy { it.name }.forEach { file ->
   require(file.length() + 4096L < TARGET_BYTES) { "Poster zu groß: ${file.name}" }
   if (size + file.length() + 4096L > TARGET_BYTES) {
    groups.add(group)
    group = mutableListOf()
    size = 4096L
   }
   group.add(file)
   size += file.length() + 4096L
  }
  groups.add(group)
  val root = DocumentsContract.buildDocumentUriUsingTree(tree, DocumentsContract.getTreeDocumentId(tree))
  val created = mutableListOf<Uri>()
  try {
   groups.forEachIndexed { index, files ->
    val filename = "SCORIUM-Backup-Teil-${(index + 1).toString().padStart(3, '0')}.zip"
    val uri = DocumentsContract.createDocument(context.contentResolver, root, "application/zip", filename)
     ?: error("Konnte $filename nicht erstellen")
    created.add(uri)
    context.contentResolver.openOutputStream(uri)?.use {
     zip(it, if (index == 0) data else null, files)
    } ?: error("Konnte $filename nicht schreiben")
    val length = context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: -1L
    require(length in 1..MAX_BYTES) { "$filename überschreitet 24 MB oder konnte nicht geprüft werden" }
   }
   save(context, current)
   return groups.size
  } catch (e: Exception) {
   created.forEach { runCatching { DocumentsContract.deleteDocument(context.contentResolver, it) } }
   throw e
  }
 }

 /** Export changed data and posters into as many <=24MB ZIP files as needed. */
 fun delta(context: Context, tree: Uri): String {
  val (data, posters) = state(context)
  val current = snapshot(data, posters)
  val previous = saved(context) ?: error("Bitte zuerst eine vollständige Sicherung erstellen.")
  val beforeKeys = previous.optJSONObject("keys") ?: JSONObject()
  val nowKeys = current.getJSONObject("keys")
  val changed = JSONObject()
  data.keys().forEach { key ->
   if (beforeKeys.optString(key, "") != nowKeys.getString(key)) changed.put(key, data.get(key))
  }
  val deletedKeys = JSONArray()
  beforeKeys.keys().forEach { key ->
   // Older snapshots also tracked personal ratings. Never emit deletion
   // records for them when upgrading from that snapshot format.
   if (ScoriumBackup.isCatalogKey(key) && !nowKeys.has(key)) deletedKeys.put(key)
  }
  val beforeImages = previous.optJSONObject("posters") ?: JSONObject()
  val nowImages = current.getJSONObject("posters")
  val changedImages = posters.filter { (name, _) ->
   beforeImages.optString(name, "") != nowImages.getString(name)
  }.values.sortedBy { it.name }
  val deletedImages = JSONArray()
  beforeImages.keys().forEach { name -> if (!nowImages.has(name)) deletedImages.put(name) }
  val manifest = JSONObject().put("format", "scorium-delta-v1")
   .put("deleted_keys", deletedKeys).put("deleted_posters", deletedImages)
  val count = changed.length() + changedImages.size + deletedKeys.length() + deletedImages.length()
  require(count > 0) { "Seit dem letzten Export gibt es keine Änderungen." }
  val overhead = changed.toString().toByteArray(Charsets.UTF_8).size.toLong() +
   manifest.toString().toByteArray(Charsets.UTF_8).size + 8192L
  require(overhead < TARGET_BYTES) { "Änderungsdaten allein überschreiten 23 MB" }
  val groups = mutableListOf<MutableList<File>>()
  var group = mutableListOf<File>()
  var size = overhead
  changedImages.forEach { file ->
   require(file.length() + 4096L < TARGET_BYTES) { "Poster zu groß: ${file.name}" }
   if (size + file.length() + 4096L > TARGET_BYTES) {
    groups.add(group)
    group = mutableListOf()
    size = 4096L
   }
   group.add(file)
   size += file.length() + 4096L
  }
  groups.add(group)
  val root = DocumentsContract.buildDocumentUriUsingTree(tree, DocumentsContract.getTreeDocumentId(tree))
  val created = mutableListOf<Uri>()
  try {
   groups.forEachIndexed { index, files ->
    val filename = "SCORIUM-Update-Teil-${(index + 1).toString().padStart(3, '0')}.zip"
    val uri = DocumentsContract.createDocument(context.contentResolver, root, "application/zip", filename)
     ?: error("Konnte $filename nicht erstellen")
    created.add(uri)
    context.contentResolver.openOutputStream(uri)?.use {
     zip(it, if (index == 0) changed else null, files, if (index == 0) manifest else null)
    } ?: error("Konnte $filename nicht schreiben")
    val length = context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: -1L
    require(length in 1..MAX_BYTES) { "$filename überschreitet 24 MB oder konnte nicht geprüft werden" }
   }
   save(context, current)
   return "$count Änderungen in ${groups.size} ZIP-Datei(en) exportiert"
  } catch (e: Exception) {
   created.forEach { runCatching { DocumentsContract.deleteDocument(context.contentResolver, it) } }
   throw e
  }
 }
}
