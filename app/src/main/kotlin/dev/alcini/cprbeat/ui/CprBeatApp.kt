package dev.alcini.cprbeat.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alcini.cprbeat.session.SessionViewModel
import dev.alcini.cprbeat.settings.LanguageStore

private enum class Screen { MAIN, SETTINGS, ABOUT }

/** Three screens, state-driven; no navigation library needed. */
@Composable
fun CprBeatApp(vm: SessionViewModel) {
    var screen by rememberSaveable { mutableStateOf(Screen.MAIN) }
    val state by vm.state.collectAsStateWithLifecycle()
    val settings by vm.settingsState.collectAsStateWithLifecycle()
    val activity = checkNotNull(LocalActivity.current)
    // Read again after every recreation, which a language change triggers.
    var language by remember { mutableStateOf(LanguageStore.current(activity)) }

    BackHandler(enabled = screen != Screen.MAIN) { screen = if (screen == Screen.ABOUT) Screen.SETTINGS else Screen.MAIN }

    when (screen) {
        Screen.MAIN -> MainScreen(
            state = state,
            snapshot = vm::snapshot,
            onStart = vm::start,
            onStop = vm::stop,
            onRate = vm::setBpm,
            onMode = vm::setMode,
            onCycleCountdown = vm::cycleCountdown,
            onOpenSettings = { screen = Screen.SETTINGS },
        )
        Screen.SETTINGS -> SettingsScreen(
            settings = settings,
            onBack = { screen = Screen.MAIN },
            onDefaultBpm = vm::setDefaultBpm,
            onDefaultCountdown = vm::setDefaultCountdown,
            onBreathPause = vm::setBreathPauseMillis,
            onAutoMaxVolume = vm::setAutoMaxVolume,
            onTonePreset = vm::setTonePreset,
            language = language,
            onLanguage = { language = it; LanguageStore.set(activity, it) },
            onOpenAbout = { screen = Screen.ABOUT },
        )
        Screen.ABOUT -> AboutScreen(onBack = { screen = Screen.SETTINGS })
    }
}
