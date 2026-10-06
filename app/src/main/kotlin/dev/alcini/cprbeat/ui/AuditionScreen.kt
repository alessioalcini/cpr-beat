package dev.alcini.cprbeat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.alcini.cprbeat.audio.ClickPlayer
import dev.alcini.cprbeat.audio.VolumeController
import dev.alcini.cprbeat.engine.CycleSpec
import dev.alcini.cprbeat.engine.Mode
import dev.alcini.cprbeat.engine.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * TEMPORARY sound-audition screen for checkpoint 1 of v0.1.0. Lets the owner hear the clicks on
 * the emulator and tune ToneBank by ear. Replaced by the real MainScreen in step 5.
 */
@Composable
fun AuditionScreen() {
    val context = LocalContext.current
    val player = remember { ClickPlayer() }
    val volume = remember { VolumeController(context) }
    val scope = rememberCoroutineScope()

    var mode by remember { mutableStateOf(Mode.COMPRESSIONS) }
    var bpm by remember { mutableIntStateOf(110) }
    var pauseSec by remember { mutableIntStateOf(5) }
    var autoMax by remember { mutableStateOf(true) }
    var playing by remember { mutableStateOf(false) }
    var position by remember { mutableStateOf<Position?>(null) }
    var frame by remember { mutableIntStateOf(0) }

    fun spec() = CycleSpec(mode, bpm, pauseSec * 1_000, player.nativeSampleRateHz)

    DisposableEffect(Unit) { onDispose { player.stop(); volume.restore() } }

    LaunchedEffect(playing) {
        while (playing) {
            withFrameNanos { }
            val f = player.frameInCycle()
            frame = f
            position = player.current?.let { if (f < it.totalFrames) it.positionAt(f) else null }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Sound audition (temporary)", style = MaterialTheme.typography.titleLarge)
            Text("Native rate: ${player.nativeSampleRateHz} Hz · alarm volume ${(volume.level() * 100).toInt()} %")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (m in Mode.entries) {
                    Button(
                        onClick = { mode = m; if (playing) scope.launch(Dispatchers.Default) { player.switchTo(spec(), keepPlace = false) } },
                        colors = if (mode == m) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                    ) { Text(if (m == Mode.COMPRESSIONS) "Compressions" else "30:2") }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (b in listOf(100, 110, 120)) {
                    Button(
                        onClick = { bpm = b; if (playing) scope.launch(Dispatchers.Default) { player.switchTo(spec(), keepPlace = true) } },
                        colors = if (bpm == b) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                    ) { Text("$b") }
                }
            }
            Text("Breath pause: $pauseSec s")
            Slider(
                value = pauseSec.toFloat(),
                onValueChange = { pauseSec = it.toInt() },
                onValueChangeFinished = { if (playing) scope.launch(Dispatchers.Default) { player.switchTo(spec(), keepPlace = true) } },
                valueRange = 3f..8f,
                steps = 4,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Auto max volume")
                Switch(checked = autoMax, onCheckedChange = { autoMax = it })
            }
            Spacer(Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth().height(96.dp),
                onClick = {
                    if (playing) {
                        playing = false
                        scope.launch(Dispatchers.Default) { player.stop(); volume.restore() }
                    } else {
                        if (autoMax) volume.raiseIfQuiet()
                        scope.launch(Dispatchers.Default) { player.start(spec()) }
                        playing = true
                    }
                },
            ) { Text(if (playing) "STOP" else "START", style = MaterialTheme.typography.headlineMedium) }
            Text("frame $frame")
            Text(
                when (val p = position) {
                    null -> "idle"
                    is Position.Compression -> "compression ${p.number} of ${p.total}" + if (p.warning) "  GET READY" else ""
                    is Position.BreathPause -> "BREATHE  ${p.elapsedFrames * 1000 / player.nativeSampleRateHz} ms  cued ${p.breathsCued}"
                },
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}
