package de.benzo.scorium

import androidx.compose.foundation.layout.size
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
 val mcu=LocalMcuCollectionTheme.current
 val accent=if(mcu) Color(0xFFE94351) else Color(0xFFB59BFF)
 IconButton(onClick=onClick,modifier=Modifier.size(36.dp),
  colors=IconButtonDefaults.iconButtonColors(
   containerColor=accent.copy(alpha=0.17f),contentColor=accent)) {
  Icon(imageVector=icon,contentDescription=description,modifier=Modifier.size(19.dp))
 }
}
