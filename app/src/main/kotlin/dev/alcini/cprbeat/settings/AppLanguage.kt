package dev.alcini.cprbeat.settings

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.core.content.edit

/** Interface language from Settings; AUTO follows the phone (SPEC 6). */
enum class AppLanguage(val tag: String?) {
    AUTO(null), ENGLISH("en"), RUSSIAN("ru");

    companion object {
        fun byTag(tag: String?): AppLanguage = entries.firstOrNull { it.tag != null && it.tag == tag } ?: AUTO
    }
}

/**
 * Stores and applies the interface language. Android 13+ keeps it in the system's per-app
 * language setting, so Settings and the system screen show the same choice. Older versions keep
 * it in SharedPreferences, read synchronously before the activity inflates anything.
 */
object LanguageStore {
    private const val PREFS = "language"
    private const val KEY = "tag"

    fun current(context: Context): AppLanguage =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            AppLanguage.byTag(context.getSystemService(LocaleManager::class.java).applicationLocales[0]?.language)
        } else {
            AppLanguage.byTag(storedTag(context))
        }

    /** The system recreates the activity on Android 13+; older versions are recreated here. */
    fun set(activity: Activity, language: AppLanguage) {
        if (language == current(activity)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.getSystemService(LocaleManager::class.java).applicationLocales =
                language.tag?.let { LocaleList.forLanguageTags(it) } ?: LocaleList.getEmptyLocaleList()
        } else {
            // Written synchronously: recreate() reads it back in attachBaseContext right away.
            activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit(commit = true) {
                if (language.tag == null) remove(KEY) else putString(KEY, language.tag)
            }
            activity.recreate()
        }
    }

    /** Base context for the activity on Android 8–12: the stored language, or the phone's. */
    fun wrap(base: Context): Context {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return base
        val tag = storedTag(base) ?: return base
        val config = Configuration(base.resources.configuration).apply { setLocales(LocaleList.forLanguageTags(tag)) }
        return base.createConfigurationContext(config)
    }

    private fun storedTag(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null)
}
