package dev.alcini.cprbeat.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.alcini.cprbeat.ui.theme.CprColor

/** Selected = filled light, unselected = outline. Used for rate, mode and settings options. */
@Composable
fun OptionButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 64.dp,
    corner: Dp = 20.dp,
    sublabel: String? = null,
) {
    val shape = RoundedCornerShape(corner)
    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(if (selected) CprColor.Selected else CprColor.Background)
            .border(BorderStroke(2.dp, if (selected) CprColor.Selected else CprColor.Outline), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = if (selected) CprColor.OnSelected else CprColor.OnBackground)
            if (sublabel != null) Text(sublabel, style = MaterialTheme.typography.labelMedium, color = if (selected) CprColor.OnSelected else CprColor.OnMuted)
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge, color = CprColor.OnMuted)
}
