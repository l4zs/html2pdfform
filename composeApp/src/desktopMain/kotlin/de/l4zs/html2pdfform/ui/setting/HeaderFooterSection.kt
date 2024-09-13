package de.l4zs.html2pdfform.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lowagie.text.HeaderFooter
import de.l4zs.html2pdfform.ui.DropdownSelector

private val aligns =
    listOf(
        HeaderFooter.ALIGN_LEFT to "Links",
        HeaderFooter.ALIGN_CENTER to "Zentriert",
        HeaderFooter.ALIGN_RIGHT to "Rechts",
        HeaderFooter.ALIGN_JUSTIFIED to "Blocksatz",
        HeaderFooter.ALIGN_TOP to "Oben",
        HeaderFooter.ALIGN_MIDDLE to "Mitte",
        HeaderFooter.ALIGN_BOTTOM to "Unten",
        HeaderFooter.ALIGN_BASELINE to "Basislinie",
        HeaderFooter.ALIGN_JUSTIFIED_ALL to "Blocksatz (alle)",
    )

@Composable
fun HeaderFooterSection(
    title: String,
    textBefore: String,
    onTextBeforeChange: (String) -> Unit,
    textAfter: String,
    onTextAfterChange: (String) -> Unit,
    numbered: Boolean,
    onNumberedChange: (Boolean) -> Unit,
    align: Int,
    onAlignChange: (Int) -> Unit,
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
                    if (it.isNotEmpty() && textAfter.isNotEmpty()) {
                        onNumberedChange(true)
                    }
                },
                label = { Text("Text vor der Seitenzahl") },
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )
            OutlinedTextField(
                value = textAfter,
                onValueChange = {
                    onTextAfterChange(it)
                    if (textBefore.isNotEmpty() && it.isNotEmpty()) {
                        onNumberedChange(true)
                    }
                },
                label = { Text("Text nach der Seitenzahl") },
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
                    text = "Seitenzahlen anzeigen",
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
                "Ausrichtung",
                aligns,
                align,
                modifier = Modifier.weight(1f),
            ) { onAlignChange(it) }
        }
    }
}
