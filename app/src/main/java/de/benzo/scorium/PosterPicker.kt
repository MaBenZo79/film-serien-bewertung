package de.benzo.scorium

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import java.io.File
import java.security.MessageDigest

internal fun posterFile(context:Context,title:String):File {
 val digest=MessageDigest.getInstance("SHA-256").digest(title.toByteArray())
 val key=digest.take(12).joinToString("") { "%02x".format(it) }
 return File(File(context.filesDir,"posters"),"$key.jpg")
}

@Composable
fun PosterPicker(title:String,onSaved:()->Unit = {}) {
 val context=LocalContext.current
 var error by remember(title) { mutableStateOf<String?>(null) }
 val pick=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
  if(uri!=null) {
   runCatching {
    val bitmap=context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
     ?: error("Bild konnte nicht gelesen werden")
    val folder=File(context.filesDir,"posters")
    folder.mkdirs()
    val target=posterFile(context,title)
    val temp=File(folder,target.name+".tmp")
    temp.outputStream().use { out ->
     if(!bitmap.compress(Bitmap.CompressFormat.JPEG,92,out)) error("Bild konnte nicht gespeichert werden")
    }
    if(!temp.renameTo(target)) {
     temp.copyTo(target,overwrite=true)
     temp.delete()
    }
    bitmap.recycle()
    onSaved()
   }.onFailure { error=it.localizedMessage ?: "Unbekannter Bildfehler" }
  }
 }
 OutlinedButton(onClick={pick.launch("image/*")}) {
  Text(if(posterFile(context,title).exists()) "Poster ändern" else "Poster auswählen")
 }
 if(error!=null) AlertDialog(onDismissRequest={error=null},
  title={Text("Poster konnte nicht gespeichert werden")},
  text={Text(error ?: "")},
  confirmButton={TextButton(onClick={error=null}) { Text("OK") }})
}
