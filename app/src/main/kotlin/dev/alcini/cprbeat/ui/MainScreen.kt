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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.foundation.Canvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.alcini.cprbeat.R
import dev.alcini.cprbeat.engine.CycleSpec
import dev.alcini.cprbeat.engine.Mode
import dev.alcini.cprbeat.engine.Position
import dev.alcini.cprbeat.session.BANNER_MILLIS
import dev.alcini.cprbeat.session.BeatSnapshot
import dev.alcini.cprbeat.session.CountdownOption
import dev.alcini.cprbeat.session.SessionState
import dev.alcini.cprbeat.ui.theme.CprColor
import dev.alcini.cprbeat.ui.theme.CprSize
import kotlinx.coroutines.launch

private const val TABULAR = "tnum"

/** The circle never shrinks below this share of its size; past it the screen scrolls (SPEC 5.1). */
private const val MIN_CIRCLE_FIT = 0.55f
private val PadTop = 24.dp
private val PadBottom = 32.dp
private val MinGap = 8.dp
private val UnderCircle = 18.dp
private val UnderStop = 14.dp
private val AboveDisclaimer = 12.dp

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
        BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding()) {
            val fit = rememberMainFit(state.volumeLow, maxWidth, maxHeight)
            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState(), enabled = fit.scroll)
                    .heightIn(min = maxHeight)
                    .padding(horizontal = CprSize.Edge).padding(top = PadTop, bottom = PadBottom),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                CountdownBlock(state, onTap = onCycleCountdown, onOpenSettings = onOpenSettings)

                ModeSelector(state.mode, onMode)

                // Fixed slots: START turns into the beat indicator in place and STOP appears in a slot
                // kept free for it, so nothing on screen moves on START or STOP (SPEC 5.1).
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.size(CprSize.StartCircle * fit.circle), contentAlignment = Alignment.Center) {
                        if (state.running) BeatArea(state, beat, fit.circle) else StartCircle(onStart, fit.circle)
                    }
                    Spacer(Modifier.height(UnderCircle))
                    Box(Modifier.height(CprSize.StopHeight)) { if (state.running) StopButton(onStop) }
                    Spacer(Modifier.height(UnderStop))
                    Box(Modifier.heightIn(min = fit.handoverHeight), contentAlignment = Alignment.Center) { HandoverLine(state) }
                    if (state.volumeLow) Text(
                        stringResource(R.string.volume_low), color = CprColor.Warning, style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.alpha(if (state.running) 1f else 0f),
                    )
                }

                Column {
                    RateRow(state.bpm, onRate)
                    // Always on screen, running too: a rescuer who joins later sees it as well (SPEC 5.1).
                    Text(
                        stringResource(R.string.main_disclaimer),
                        style = MaterialTheme.typography.bodySmall, color = CprColor.OnMuted, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = AboveDisclaimer),
                    )
                }
            }
        }
        if (flash.value > 0f) Box(Modifier.fillMaxSize().alpha(flash.value).background(CprColor.OnBackground))
    }
}

/** How far the circle shrinks ([circle], 1 on ordinary phones) and the height kept for the handover line. */
private class MainFit(raw: Float, val handoverHeight: Dp) {
    val circle = raw.coerceIn(MIN_CIRCLE_FIT, 1f)
    /** Even the smallest circle does not fit: let the screen scroll. */
    val scroll = raw < MIN_CIRCLE_FIT
}

/**
 * Sizes the circle so the whole screen fits: full size on ordinary phones, smaller on short screens
 * and with a larger display size or font (SPEC 5.1). Every other element keeps its size. Idle and
 * running share one layout, so the result does not depend on the session state.
 */
@Composable
private fun rememberMainFit(volumeLow: Boolean, maxWidth: Dp, maxHeight: Dp): MainFit {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val type = MaterialTheme.typography
    val disclaimer = stringResource(R.string.main_disclaimer)
    val warning = stringResource(R.string.volume_low)
    return remember(volumeLow, maxWidth, maxHeight, density, disclaimer) {
        with(density) {
            val width = maxWidth - CprSize.Edge * 2
            fun textHeight(text: String, style: TextStyle) =
                measurer.measure(text, style, constraints = Constraints(maxWidth = width.roundToPx())).size.height.toDp()
            val handoverHeight = textHeight("00:00", type.titleLarge)
            val fixed = PadTop + CprSize.TimerBlock + CprSize.Segmented + CprSize.RateButton + AboveDisclaimer +
                textHeight(disclaimer, type.bodySmall) + PadBottom + MinGap * 3
            val below = UnderCircle + CprSize.StopHeight + UnderStop + handoverHeight +
                (if (volumeLow) textHeight(warning, type.bodyMedium) else 0.dp)
            val raw = minOf((maxHeight - fixed - below) / CprSize.StartCircle, width / CprSize.StartCircle)
            MainFit(raw, handoverHeight)
        }
    }
}

@Composable
private fun CountdownBlock(state: SessionState, onTap: () -> Unit, onOpenSettings: () -> Unit) {
    val off = state.countdown == CountdownOption.OFF
    Box(Modifier.fillMaxWidth().height(CprSize.TimerBlock)) {
        // The banner covers the digits for BANNER_MILLIS; the countdown keeps running underneath
        // and taps are ignored so the interval cannot be changed by accident (SPEC 5.4).
        if (state.bannerRemainingMillis > 0) SwitchBanner(state.bannerRemainingMillis, Modifier.align(Alignment.Center))
        else Column(
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
        }
        IconButton(onClick = onOpenSettings, modifier = Modifier.align(Alignment.TopEnd).size(CprSize.Gear)) {
            Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings), tint = CprColor.OnMuted, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun ModeSelector(mode: Mode, onMode: (Mode) -> Unit) {
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
private fun SwitchBanner(remainingMillis: Long, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().height(CprSize.SwitchBanner).clip(RoundedCornerShape(24.dp)).background(CprColor.Beat),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(stringResource(R.string.switch_rescuer), style = MaterialTheme.typography.headlineMedium, color = CprColor.OnBeat)
        Spacer(Modifier.height(8.dp))
        Box(Modifier.width(240.dp).height(6.dp).clip(RoundedCornerShape(3.dp)).background(CprColor.OnBeat.copy(alpha = 0.25f))) {
            Box(Modifier.fillMaxWidth(remainingMillis / BANNER_MILLIS.toFloat()).height(6.dp).background(CprColor.OnBeat))
        }
    }
}

@Composable
private fun StartCircle(onStart: () -> Unit, fit: Float) {
    Box(
        Modifier.size(CprSize.StartCircle * fit).clip(CircleShape).background(CprColor.Beat).clickable(onClick = onStart),
        contentAlignment = Alignment.Center,
    ) { Text(stringResource(R.string.start), style = MaterialTheme.typography.displaySmall.shrink(fit), color = CprColor.OnBeat) }
}

@Composable
private fun BeatArea(state: SessionState, beat: BeatSnapshot, fit: Float) {
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
    Box(Modifier.size(CprSize.BeatArea * fit), contentAlignment = Alignment.Center) {
        if (state.mode == Mode.THIRTY_TWO) TickRing(position, fit)
        val diskColor = if (state.mode == Mode.COMPRESSIONS) lerp(CprColor.SurfaceHigh, CprColor.Beat, fill.value) else CprColor.SurfaceHigh
        Box(
            Modifier.size(CprSize.BeatDisk * fit).scale(scale.value).clip(CircleShape).background(diskColor).border(BorderStroke(4.dp, outline), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            when (position) {
                is Position.Compression -> if (state.mode == Mode.THIRTY_TWO) Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${position.number}", style = MaterialTheme.typography.displayLarge.shrink(fit).copy(fontFeatureSettings = TABULAR), color = CprColor.OnBackground)
                    if (position.warning) Text(stringResource(R.string.get_ready), style = MaterialTheme.typography.labelLarge, color = CprColor.Warning)
                    else Text("OF ${position.total}", style = MaterialTheme.typography.labelLarge, color = CprColor.OnMuted)
                }
                is Position.BreathPause -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.breathe), style = MaterialTheme.typography.headlineMedium.shrink(fit), color = CprColor.Breathe)
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

/** Scales a style down with the circle so text keeps its proportion inside it. */
private fun TextStyle.shrink(fit: Float) =
    if (fit >= 1f) this else copy(fontSize = fontSize * fit, lineHeight = if (lineHeight.isSp) lineHeight * fit else lineHeight)

private fun lerp(a: Color, b: Color, t: Float) = Color(
    a.red + (b.red - a.red) * t, a.green + (b.green - a.green) * t, a.blue + (b.blue - a.blue) * t, 1f,
)

@Composable
private fun TickRing(position: Position?, fit: Float) {
    val done = when (position) { is Position.Compression -> position.number; is Position.BreathPause -> CycleSpec.COMPRESSIONS_PER_CYCLE; null -> 0 }
    val zoneStart = CycleSpec.COMPRESSIONS_PER_CYCLE - CycleSpec.WARNING_ZONE_SIZE
    Canvas(Modifier.size(CprSize.BeatArea * fit)) {
        val radius = 124.dp.toPx() * fit
        for (i in 0 until CycleSpec.COMPRESSIONS_PER_CYCLE) {
            val zone = i >= zoneStart
            val color = when { i >= done -> CprColor.TickIdle; zone -> CprColor.Warning; else -> CprColor.TickDone }
            val w = (if (zone) 8.dp else 5.dp).toPx() * fit
            val h = (if (zone) 24.dp else 14.dp).toPx() * fit
            rotate(degrees = i * 12f + 6f, pivot = center) {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(center.x - w / 2, center.y - radius - h / 2),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(3.dp.toPx()),
                )
            }
        }
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
