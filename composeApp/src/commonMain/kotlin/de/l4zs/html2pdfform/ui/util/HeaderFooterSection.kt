package de.l4zs.html2pdfform.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
                label = { Text("Text vor der Seitenzahl") },
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )
            OutlinedTextField(
                value = textAfter,
                onValueChange = {
                    onTextAfterChange(it)
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
                Align.entries,
                align,
                modifier = Modifier.weight(1f),
            ) { onAlignChange(it) }
        }
    }
}
