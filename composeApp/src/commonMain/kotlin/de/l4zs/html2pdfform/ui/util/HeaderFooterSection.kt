package de.l4zs.html2pdfform.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.l4zs.html2pdfform.backend.data.Align
import de.l4zs.html2pdfform.resources.*
import org.jetbrains.compose.resources.stringResource

/**
 * A section for header and footer settings.
 *
 * @param title The title of the section.
 * @param textBefore The text before the page number.
 * @param onTextBeforeChange The callback for changing the text before the page number.
 * @param textAfter The text after the page number.
 * @param onTextAfterChange The callback for changing the text after the page number.
 * @param numbered Whether the page number should be shown.
 * @param onNumberedChange The callback for changing the page number visibility.
 * @param align The alignment of the text.
 * @param onAlignChange The callback for changing the text alignment.
 */
@Composable
fun HeaderFooterSection(
    title: String,
    textBefore: String,
    onTextBeforeChange: (String) -> Unit,
    textAfter: String,
    onTextAfterChange: (String) -> Unit,
    numbered: Boolean,
    onNumberedChange: (Boolean) -> Unit,
    align: Align,
    onAlignChange: (Align) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, fontSize = 16.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = textBefore,
                onValueChange = {
                    onTextBeforeChange(it)
                },
                label = { Text(stringResource(Res.string.settings_page_section_headerfooter_before)) },
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )
            OutlinedTextField(
                value = textAfter,
                onValueChange = {
                    onTextAfterChange(it)
                },
                label = { Text(stringResource(Res.string.settings_page_section_headerfooter_after)) },
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Checkbox(
                    checked = numbered,
                    onCheckedChange = {
                        onNumberedChange(it)
                        if (textBefore.isNotEmpty() && textAfter.isNotEmpty()) {
                            onNumberedChange(true)
                        }
                    },
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.settings_page_section_headerfooter_numbered),
                    style = MaterialTheme.typography.body1,
                    modifier =
                        Modifier.clickable {
                            onNumberedChange(!numbered)
                            if (textBefore.isNotEmpty() && textAfter.isNotEmpty()) {
                                onNumberedChange(true)
                            }
                        },
                )
            }
            DropdownSelector(
                stringResource(Res.string.settings_page_section_headerfooter_align),
                Align.entries,
                align,
                modifier = Modifier.weight(1f),
            ) { onAlignChange(it) }
        }
    }
}
