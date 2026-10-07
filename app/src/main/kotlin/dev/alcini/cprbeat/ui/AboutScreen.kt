package dev.alcini.cprbeat.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.alcini.cprbeat.R
import dev.alcini.cprbeat.ui.theme.CprColor
import dev.alcini.cprbeat.ui.theme.CprSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val version = remember { runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull() ?: "?" }
    var showLicenses by remember { mutableStateOf(false) }
    val notices by produceState<String?>(initialValue = null, showLicenses) {
        if (showLicenses && value == null) value = withContext(Dispatchers.IO) {
            runCatching { context.assets.open("THIRD_PARTY_NOTICES.md").bufferedReader().readText() }.getOrDefault("")
        }
    }
    val sourceUrl = stringResource(R.string.about_source_url)
    val licenseUrl = stringResource(R.string.about_license_url)
    val privacyUrl = stringResource(R.string.privacy_policy_url)
    val email = stringResource(R.string.contact_email)

    Column(Modifier.fillMaxSize().background(CprColor.Background).safeDrawingPadding()) {
        ScreenHeader(stringResource(R.string.about), onBack)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = CprSize.Edge).padding(top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(CprColor.Surface)) {
                AboutRow(stringResource(R.string.app_name), version)
                LinkRow(stringResource(R.string.about_license_name), label = stringResource(R.string.about_license)) {
                    context.open(Intent(Intent.ACTION_VIEW, licenseUrl.toUri()))
                }
                LinkRow(stringResource(R.string.about_source)) { context.open(Intent(Intent.ACTION_VIEW, sourceUrl.toUri())) }
                LinkRow(stringResource(R.string.privacy_policy)) { context.open(Intent(Intent.ACTION_VIEW, privacyUrl.toUri())) }
                LinkRow(email, label = stringResource(R.string.contact)) {
                    context.open(Intent(Intent.ACTION_SENDTO, "mailto:$email".toUri()).putExtra(Intent.EXTRA_SUBJECT, "CPR Beat $version"))
                }
            }
            Text(stringResource(R.string.about_disclaimer), style = MaterialTheme.typography.bodyLarge, color = CprColor.OnBackground)
            Text(stringResource(R.string.about_guidelines), style = MaterialTheme.typography.bodyMedium, color = CprColor.OnMuted)
            Text(stringResource(R.string.about_copyright), style = MaterialTheme.typography.bodyMedium, color = CprColor.OnMuted)
            SectionLabel(stringResource(R.string.how_to_use))
            Text(stringResource(R.string.how_to_use_text), style = MaterialTheme.typography.bodyLarge, color = CprColor.OnBackground)
            SectionLabel(stringResource(R.string.privacy))
            Text(stringResource(R.string.privacy_text), style = MaterialTheme.typography.bodyLarge, color = CprColor.OnBackground)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                SectionLabel(stringResource(R.string.third_party))
                Text(
                    stringResource(if (showLicenses) R.string.hide else R.string.show),
                    style = MaterialTheme.typography.titleMedium, color = CprColor.Beat,
                    modifier = Modifier.clickable { showLicenses = !showLicenses }.padding(12.dp),
                )
            }
            if (showLicenses) Text(
                notices ?: "…",
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                color = CprColor.OnMuted,
            )
        }
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = CprColor.OnBackground)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = CprColor.OnMuted)
    }
}

@Composable
private fun LinkRow(text: String, label: String? = null, onClick: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().heightIn(min = 64.dp).clickable(onClick = onClick).padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        if (label != null) Text(label, style = MaterialTheme.typography.bodyMedium, color = CprColor.OnMuted)
        Text(text, style = MaterialTheme.typography.bodyLarge, color = CprColor.Beat)
    }
}

/** No browser or mail app installed: do nothing rather than crash; the address is on screen. */
private fun Context.open(intent: Intent) {
    runCatching { startActivity(intent) }
}
