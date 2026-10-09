package dev.alcini.cprbeat

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alcini.cprbeat.session.SessionViewModel
import dev.alcini.cprbeat.settings.LanguageStore
import dev.alcini.cprbeat.ui.CprBeatApp
import dev.alcini.cprbeat.ui.theme.CprBeatTheme

class MainActivity : ComponentActivity() {
    private val vm: SessionViewModel by viewModels()

    /** Language chosen in Settings on Android 8–12; 13+ applies it by itself (SPEC 6). */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageStore.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_ALARM
        setContent {
            CprBeatTheme {
                val state by vm.state.collectAsStateWithLifecycle()
                // The screen stays on while the metronome runs (SPEC 5.1).
                DisposableEffect(state.running) {
                    if (state.running) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    onDispose { window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
                }
                CprBeatApp(vm)
            }
        }
    }

    /** Home, lock button or an incoming call: the metronome stops (SPEC 5.1). */
    override fun onStop() {
        vm.onLeftForeground()
        super.onStop()
    }
}
