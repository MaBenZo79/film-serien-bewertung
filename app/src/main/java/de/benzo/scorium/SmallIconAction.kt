package de.benzo.scorium

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SmallIconAction(icon:ImageVector,description:String,onClick:()->Unit) {
 val v=scoriumVisuals()
 IconButton(onClick=onClick,modifier=Modifier.size(39.dp).border(1.dp,v.border,CircleShape),
  colors=IconButtonDefaults.iconButtonColors(
   containerColor=v.elevated,contentColor=v.accent)) {
  Icon(imageVector=icon,contentDescription=description,modifier=Modifier.size(19.dp))
 }
}
