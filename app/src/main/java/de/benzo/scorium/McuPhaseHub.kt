package de.benzo.scorium

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val mcuRed = Color(0xFFCE232D)

/** Two-column MCU hub; every image tile has the same fixed 4:3 aspect ratio. */
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
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBack) { Text("← SCORIUM", color = Color.White) }
        Spacer(Modifier.height(22.dp))
        Image(
            painter = painterResource(R.drawable.scorium_mcu_logo),
            contentDescription = "MARVEL STUDIOS MCU",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )
        Spacer(Modifier.height(26.dp))
        val phases = listOf("Komplett", "Phase 1", "Phase 2", "Phase 3", "Phase 4", "Phase 5", "Phase 6")
        phases.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { phase ->
                    Column(Modifier.weight(1f)) {
                        Box(
                            Modifier.fillMaxWidth()
                                .aspectRatio(4f / 3f)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.5.dp, mcuRed, RoundedCornerShape(14.dp))
                                .clickable { onPhase(phase) }
                        ) {
                            ScoriumPoster(
                                "collection::mcu::${phase}",
                                Modifier.fillMaxSize(),
                                revision = revision,
                                isCollection = true,
                                borderColor = mcuRed
                            )
                            Box(
                                Modifier.align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .background(Color(0xCC300407))
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Text(phase, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                        // Each phase retains its own editable poster key.
                        if(BuildConfig.SCORIUM_ADMIN) PosterPicker("collection::mcu::${phase}", onImageSaved)
                    }
                }
                if(pair.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
        }
        Spacer(Modifier.height(20.dp))
    }
}
