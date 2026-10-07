package dev.alcini.cprbeat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.alcini.cprbeat.R
import dev.alcini.cprbeat.engine.ToneBank
import dev.alcini.cprbeat.session.CountdownOption
import dev.alcini.cprbeat.settings.Settings
import dev.alcini.cprbeat.ui.theme.CprColor
import dev.alcini.cprbeat.ui.theme.CprSize

@Composable
fun ScreenHeader(title: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack, modifier = Modifier.size(64.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = CprColor.OnBackground, modifier = Modifier.size(28.dp))
        }
        Text(title, style = MaterialTheme.typography.titleLarge, color = CprColor.OnBackground)
    }
}

@Composable
fun SettingsScreen(
    settings: Settings,
    onBack: () -> Unit,
    onDefaultBpm: (Int) -> Unit,
    onDefaultCountdown: (CountdownOption) -> Unit,
    onBreathPause: (Int) -> Unit,
    onAutoMaxVolume: (Boolean) -> Unit,
    onTonePreset: (String) -> Unit,
    onOpenAbout: () -> Unit,
) {
    Column(Modifier.fillMaxSize().background(CprColor.Background).safeDrawingPadding()) {
        ScreenHeader(stringResource(R.string.settings), onBack)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = CprSize.Edge).padding(top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionLabel(stringResource(R.string.default_rate))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (r in listOf(100, 110, 120)) OptionButton("$r", settings.defaultBpm == r, { onDefaultBpm(r) }, Modifier.weight(1f))
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionLabel(stringResource(R.string.default_countdown))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (o in CountdownOption.entries) {
                        val label = if (o == CountdownOption.OFF) stringResource(R.string.off).lowercase().replaceFirstChar { it.uppercase() } else "${o.minutes} min"
                        OptionButton(label, settings.defaultCountdown == o, { onDefaultCountdown(o) }, Modifier.weight(1f))
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                var pauseSec by remember(settings.breathPauseMillis) { mutableFloatStateOf(settings.breathPauseMillis / 1000f) }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    SectionLabel(stringResource(R.string.breath_pause))
                    Text("${pauseSec.toInt()} s", style = MaterialTheme.typography.titleMedium, color = CprColor.OnBackground)
                }
                Slider(
                    value = pauseSec,
                    onValueChange = { pauseSec = it },
                    onValueChangeFinished = { onBreathPause(pauseSec.toInt() * 1000) },
                    valueRange = 3f..8f,
                    steps = 4,
                    modifier = Modifier.height(64.dp),
                )
                Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("3 s", style = MaterialTheme.typography.bodyMedium, color = CprColor.OnMuted)
                    Text("8 s", style = MaterialTheme.typography.bodyMedium, color = CprColor.OnMuted)
                }
            }
            Row(
                Modifier.fillMaxWidth().heightIn(min = 72.dp).clip(RoundedCornerShape(20.dp)).background(CprColor.Surface)
                    .clickable { onAutoMaxVolume(!settings.autoMaxVolume) }.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.auto_max_volume), style = MaterialTheme.typography.bodyLarge, color = CprColor.OnBackground)
                    Text(stringResource(R.string.auto_max_volume_hint), style = MaterialTheme.typography.bodyMedium, color = CprColor.OnMuted)
                }
                Switch(
                    checked = settings.autoMaxVolume,
                    onCheckedChange = onAutoMaxVolume,
                    colors = SwitchDefaults.colors(checkedTrackColor = CprColor.Beat, checkedThumbColor = CprColor.OnBeat),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionLabel(stringResource(R.string.click_sound))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (bank in listOf(ToneBank.LOW, ToneBank.WOOD, ToneBank.CLEAN)) {
                        val label = stringResource(when (bank) { ToneBank.LOW -> R.string.tone_low; ToneBank.WOOD -> R.string.tone_medium; else -> R.string.tone_high })
                        OptionButton(label, settings.tonePreset == bank.name, { onTonePreset(bank.name) }, Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            OptionButton(stringResource(R.string.about), selected = false, onClick = onOpenAbout, modifier = Modifier.fillMaxWidth())
        }
    }
}
