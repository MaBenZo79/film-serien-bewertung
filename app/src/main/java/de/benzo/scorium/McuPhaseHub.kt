package de.benzo.scorium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Dedicated MCU destination. Phase card artwork is saved independently by PosterPicker. */
@Composable
fun McuPhaseHub(
    revision: Int,
    onImageSaved: () -> Unit,
    onBack: () -> Unit,
    onPhase: (String) -> Unit
) {
    Column(
        Modifier.fillMaxSize()
            .background(Color(0xFF300407))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        TextButton(onClick = onBack) { Text("← SCORIUM", color = Color.White) }
        Spacer(Modifier.height(16.dp))
        Text(
            "MARVEL STUDIOS MCU",
            fontSize = 29.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFF1E9E9)
        )
        Spacer(Modifier.height(8.dp))
        Text("Deine MCU-Sammlung", color = Color(0xFFDDADAD), fontSize = 14.sp)
        Spacer(Modifier.height(26.dp))
        val phases = listOf("Komplett", "Phase 1", "Phase 2", "Phase 3", "Phase 4", "Phase 5", "Phase 6")
        phases.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                pair.forEach { phase ->
                    Box(Modifier.weight(1f)) {
                        // Same editable collection card component as the home screen.
                        ScoriumCollectionTile(
                            phase, "MCU Filme & Serien", "collection::mcu::${phase}",
                            onClick = { onPhase(phase) },
                            onImageSaved = onImageSaved,
                            revision = revision
                        )
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
        }
        Spacer(Modifier.height(32.dp))
    }
}
