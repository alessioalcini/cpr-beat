package dev.alcini.cprbeat.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.alcini.cprbeat.R
import dev.alcini.cprbeat.engine.CycleSpec
import dev.alcini.cprbeat.engine.Mode
import dev.alcini.cprbeat.engine.Position
import dev.alcini.cprbeat.session.BeatSnapshot
import dev.alcini.cprbeat.session.CountdownOption
import dev.alcini.cprbeat.session.SessionState
import dev.alcini.cprbeat.ui.theme.CprColor
import dev.alcini.cprbeat.ui.theme.CprSize
import kotlinx.coroutines.launch

private const val TABULAR = "tnum"

@Composable
fun MainScreen(
    state: SessionState,
    snapshot: () -> BeatSnapshot,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onRate: (Int) -> Unit,
    onMode: (Mode) -> Unit,
    onCycleCountdown: () -> Unit,
    onOpenSettings: () -> Unit,
    onAnyTap: () -> Unit,
) {
    var beat by remember { mutableStateOf(BeatSnapshot(null, 0)) }
    LaunchedEffect(state.running) {
        if (!state.running) { beat = BeatSnapshot(null, 0); return@LaunchedEffect }
        while (true) { withFrameNanos { }; beat = snapshot() }
    }
    val flash = remember { Animatable(0f) }
    LaunchedEffect(state.flashCount) {
        if (state.flashCount == 0) return@LaunchedEffect
        flash.snapTo(0.85f); flash.animateTo(0f, tween(500))
    }

    Box(Modifier.fillMaxSize().background(CprColor.Background)) {
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = CprSize.Edge).padding(top = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            CountdownBlock(state, onTap = { onAnyTap(); onCycleCountdown() }, onOpenSettings = { onAnyTap(); onOpenSettings() })

            if (state.bannerRemainingMillis > 0) SwitchBanner(state.bannerRemainingMillis)
            else ModeSelector(state.mode, enabled = true) { onAnyTap(); onMode(it) }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                if (state.running) {
                    BeatArea(state, beat)
                    Spacer(Modifier.height(18.dp))
                    StopButton(onStop)
                    Spacer(Modifier.height(14.dp))
                    HandoverLine(state)
                    if (state.volumeLow) Text(stringResource(R.string.volume_low), color = CprColor.Warning, style = MaterialTheme.typography.bodyMedium)
                } else {
                    StartCircle(onStart = { onAnyTap(); onStart() })
                    Spacer(Modifier.height(18.dp))
                    if (state.showHints) Text(stringResource(R.string.hint_start), color = CprColor.Beat, style = MaterialTheme.typography.bodyLarge)
                    else if (state.startedAtEpochMillis != null) HandoverLine(state)
                }
            }

            Column {
                if (state.showHints && !state.running) {
                    Text(stringResource(R.string.hint_rate), color = CprColor.Beat, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 6.dp))
                }
                RateRow(state.bpm) { onAnyTap(); onRate(it) }
            }
        }
        if (flash.value > 0f) Box(Modifier.fillMaxSize().alpha(flash.value).background(CprColor.OnBackground))
    }
}

@Composable
private fun CountdownBlock(state: SessionState, onTap: () -> Unit, onOpenSettings: () -> Unit) {
    val off = state.countdown == CountdownOption.OFF
    Box(Modifier.fillMaxWidth().height(CprSize.TimerBlock)) {
        Column(
            modifier = Modifier.fillMaxSize().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onTap),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(stringResource(if (off) R.string.switch_timer else R.string.switch_in), style = MaterialTheme.typography.labelLarge, color = CprColor.OnMuted)
            Text(
                text = if (off) stringResource(R.string.off) else formatCountdown(state.countdownRemainingMillis ?: state.countdown.millis),
                style = MaterialTheme.typography.displayMedium.copy(fontFeatureSettings = TABULAR),
                color = if (off) CprColor.OnMuted else CprColor.OnBackground,
            )
            if (state.showHints && !state.running) Text(stringResource(R.string.hint_countdown), color = CprColor.Beat, style = MaterialTheme.typography.bodyMedium)
        }
        IconButton(onClick = onOpenSettings, modifier = Modifier.align(Alignment.TopEnd).size(CprSize.Gear)) {
            Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings), tint = CprColor.OnMuted, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun ModeSelector(mode: Mode, enabled: Boolean, onMode: (Mode) -> Unit) {
    val shape = RoundedCornerShape(32.dp)
    Row(
        Modifier.fillMaxWidth().height(CprSize.Segmented).clip(shape).border(BorderStroke(2.dp, CprColor.Outline), shape),
    ) {
        Segment(stringResource(R.string.mode_compressions), mode == Mode.COMPRESSIONS, Modifier.weight(1f)) { onMode(Mode.COMPRESSIONS) }
        Box(Modifier.width(2.dp).fillMaxSize().background(CprColor.Outline))
        Segment(stringResource(R.string.mode_thirty_two), mode == Mode.THIRTY_TWO, Modifier.weight(1f)) { onMode(Mode.THIRTY_TWO) }
    }
}

@Composable
private fun Segment(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier.fillMaxSize().background(if (selected) CprColor.Selected else CprColor.Background).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Text(label, style = MaterialTheme.typography.titleMedium, color = if (selected) CprColor.OnSelected else CprColor.OnBackground) }
}

@Composable
private fun SwitchBanner(remainingMillis: Long) {
    Column(
        Modifier.fillMaxWidth().height(CprSize.SwitchBanner).clip(RoundedCornerShape(24.dp)).background(CprColor.Beat),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(stringResource(R.string.switch_rescuer), style = MaterialTheme.typography.headlineMedium, color = CprColor.OnBeat)
        Spacer(Modifier.height(8.dp))
        Box(Modifier.width(240.dp).height(6.dp).clip(RoundedCornerShape(3.dp)).background(CprColor.OnBeat.copy(alpha = 0.25f))) {
            Box(Modifier.fillMaxWidth(remainingMillis / 10_000f).height(6.dp).background(CprColor.OnBeat))
        }
    }
}

@Composable
private fun StartCircle(onStart: () -> Unit) {
    Box(
        Modifier.size(CprSize.StartCircle).clip(CircleShape).background(CprColor.Beat).clickable(onClick = onStart),
        contentAlignment = Alignment.Center,
    ) { Text(stringResource(R.string.start), style = MaterialTheme.typography.displaySmall, color = CprColor.OnBeat) }
}

@Composable
private fun BeatArea(state: SessionState, beat: BeatSnapshot) {
    val position = beat.position
    val scale = remember { Animatable(1f) }
    val fill = remember { Animatable(0f) }
    LaunchedEffect(beat.beatsHeard) {
        if (beat.beatsHeard == 0L) return@LaunchedEffect
        scale.snapTo(0.93f); fill.snapTo(1f)
        launch { scale.animateTo(1f, tween(240)) }
        fill.animateTo(0f, tween(260))
    }
    val breathing = position is Position.BreathPause
    val warning = (position as? Position.Compression)?.warning == true
    val outline = when { breathing -> CprColor.Breathe; warning -> CprColor.Warning; else -> CprColor.OutlineSubtle }
    Box(Modifier.size(CprSize.BeatArea), contentAlignment = Alignment.Center) {
        if (state.mode == Mode.THIRTY_TWO) TickRing(position)
        val diskColor = if (state.mode == Mode.COMPRESSIONS) lerp(CprColor.SurfaceHigh, CprColor.Beat, fill.value) else CprColor.SurfaceHigh
        Box(
            Modifier.size(CprSize.BeatDisk).scale(scale.value).clip(CircleShape).background(diskColor).border(BorderStroke(4.dp, outline), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            when (position) {
                is Position.Compression -> if (state.mode == Mode.THIRTY_TWO) Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${position.number}", style = MaterialTheme.typography.displayLarge.copy(fontFeatureSettings = TABULAR), color = CprColor.OnBackground)
                    if (position.warning) Text(stringResource(R.string.get_ready), style = MaterialTheme.typography.labelLarge, color = CprColor.Warning)
                    else Text("OF ${position.total}", style = MaterialTheme.typography.labelLarge, color = CprColor.OnMuted)
                }
                is Position.BreathPause -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.breathe), style = MaterialTheme.typography.headlineMedium, color = CprColor.Breathe)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        repeat(2) { i ->
                            Box(Modifier.size(28.dp).clip(CircleShape).border(BorderStroke(4.dp, CprColor.Breathe), CircleShape)
                                .background(if (i < position.breathsCued) CprColor.Breathe else Color.Transparent))
                        }
                    }
                }
                null -> Unit
            }
        }
    }
}

private fun lerp(a: Color, b: Color, t: Float) = Color(
    a.red + (b.red - a.red) * t, a.green + (b.green - a.green) * t, a.blue + (b.blue - a.blue) * t, 1f,
)

@Composable
private fun TickRing(position: Position?) {
    val done = when (position) { is Position.Compression -> position.number; is Position.BreathPause -> CycleSpec.COMPRESSIONS_PER_CYCLE; null -> 0 }
    val zoneStart = CycleSpec.COMPRESSIONS_PER_CYCLE - CycleSpec.WARNING_ZONE_SIZE
    for (i in 0 until CycleSpec.COMPRESSIONS_PER_CYCLE) {
        val zone = i >= zoneStart
        val color = when { zone -> CprColor.Warning; i < done -> CprColor.TickDone; else -> CprColor.TickIdle }
        Box(
            Modifier
                .graphicsLayer { rotationZ = i * 12f + 6f; translationY = -124.dp.toPx() }
                .size(width = if (zone) 8.dp else 5.dp, height = if (zone) 24.dp else 14.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color),
        )
    }
}

@Composable
private fun StopButton(onStop: () -> Unit) {
    val shape = RoundedCornerShape(32.dp)
    Box(
        Modifier.size(CprSize.StopWidth, CprSize.StopHeight).clip(shape).border(BorderStroke(2.dp, CprColor.Stop), shape).clickable(onClick = onStop),
        contentAlignment = Alignment.Center,
    ) { Text(stringResource(R.string.stop), style = MaterialTheme.typography.titleLarge, color = CprColor.Stop) }
}

@Composable
private fun HandoverLine(state: SessionState) {
    val started = state.startedAtEpochMillis ?: return
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(stringResource(R.string.cpr_elapsed), style = MaterialTheme.typography.bodyLarge, color = CprColor.OnMuted)
        Text(formatElapsed(state.elapsedMillis), style = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = TABULAR), color = CprColor.OnBackground)
        Text("·", color = CprColor.Outline)
        Text(stringResource(R.string.started_at), style = MaterialTheme.typography.bodyLarge, color = CprColor.OnMuted)
        Text(formatClock(started), style = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = TABULAR), color = CprColor.OnBackground)
    }
}

@Composable
private fun RateRow(bpm: Int, onRate: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        for (r in listOf(100, 110, 120)) {
            OptionButton("$r", selected = bpm == r, onClick = { onRate(r) }, modifier = Modifier.weight(1f), height = CprSize.RateButton, corner = 24.dp, sublabel = stringResource(R.string.bpm))
        }
    }
}
