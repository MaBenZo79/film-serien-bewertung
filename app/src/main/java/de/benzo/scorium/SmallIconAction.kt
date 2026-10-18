package de.benzo.scorium

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SmallIconAction(icon:ImageVector,description:String,onClick:()->Unit) {
 FilledTonalIconButton(onClick=onClick,modifier=Modifier.size(40.dp),shape=CircleShape) {
  Icon(imageVector=icon,contentDescription=description,modifier=Modifier.size(20.dp))
 }
}
