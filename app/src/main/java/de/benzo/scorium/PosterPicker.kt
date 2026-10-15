package de.benzo.scorium

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*

@Composable
fun PosterPicker(title: String) {
 val context=LocalContext.current
 val prefs=remember { context.getSharedPreferences("scorium_posters",Context.MODE_PRIVATE) }
 var poster by remember(title) { mutableStateOf(prefs.getString(title,null)) }
 val pick=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
  if(uri!=null) {
   runCatching { context.contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION) }
   prefs.edit().putString(title,uri.toString()).apply()
   poster=uri.toString()
  }
 }
 OutlinedButton(onClick={pick.launch(arrayOf("image/*"))}) {
  Text(if(poster==null) "Eigenes Poster auswählen" else "Poster ändern")
 }
}
