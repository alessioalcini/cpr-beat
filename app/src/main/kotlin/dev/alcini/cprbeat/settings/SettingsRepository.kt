package dev.alcini.cprbeat.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.alcini.cprbeat.engine.CycleSpec
import dev.alcini.cprbeat.engine.Tempo
import dev.alcini.cprbeat.session.CountdownOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** DataStore-backed settings. One instance per process is enough; it is cheap to create. */
class SettingsRepository(context: Context) {
    private val store = context.applicationContext.dataStore

    val settings: Flow<Settings> = store.data.map { p ->
        Settings(
            defaultBpm = (p[KEY_BPM] ?: Tempo.DEFAULT_BPM).coerceIn(Tempo.MIN_BPM, Tempo.MAX_BPM),
            defaultCountdown = CountdownOption.fromMinutes(p[KEY_COUNTDOWN_MIN] ?: CountdownOption.DEFAULT.minutes),
            breathPauseMillis = (p[KEY_BREATH_PAUSE_MS] ?: CycleSpec.DEFAULT_BREATH_PAUSE_MILLIS)
                .coerceIn(CycleSpec.MIN_BREATH_PAUSE_MILLIS, CycleSpec.MAX_BREATH_PAUSE_MILLIS),
            autoMaxVolume = p[KEY_AUTO_MAX_VOLUME] ?: true,
            tonePreset = p[KEY_TONE_PRESET] ?: Settings().tonePreset,
            hintsDismissed = p[KEY_HINTS_DISMISSED] ?: false,
        )
    }

    suspend fun setDefaultBpm(bpm: Int) = store.edit { it[KEY_BPM] = bpm }
    suspend fun setDefaultCountdown(option: CountdownOption) = store.edit { it[KEY_COUNTDOWN_MIN] = option.minutes }
    suspend fun setBreathPauseMillis(millis: Int) = store.edit { it[KEY_BREATH_PAUSE_MS] = millis }
    suspend fun setAutoMaxVolume(on: Boolean) = store.edit { it[KEY_AUTO_MAX_VOLUME] = on }
    suspend fun setTonePreset(name: String) = store.edit { it[KEY_TONE_PRESET] = name }
    suspend fun setHintsDismissed() = store.edit { it[KEY_HINTS_DISMISSED] = true }

    private companion object {
        val KEY_BPM = intPreferencesKey("default_bpm")
        val KEY_COUNTDOWN_MIN = intPreferencesKey("default_countdown_minutes")
        val KEY_BREATH_PAUSE_MS = intPreferencesKey("breath_pause_millis")
        val KEY_AUTO_MAX_VOLUME = booleanPreferencesKey("auto_max_volume")
        val KEY_TONE_PRESET = stringPreferencesKey("tone_preset")
        val KEY_HINTS_DISMISSED = booleanPreferencesKey("hints_dismissed")
    }
}
