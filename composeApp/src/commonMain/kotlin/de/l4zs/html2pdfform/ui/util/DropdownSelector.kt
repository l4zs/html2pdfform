package de.l4zs.html2pdfform.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import de.l4zs.html2pdfform.backend.data.Nameable

@Composable
fun <T : Nameable> DropdownSelector(
    label: String,
    options: List<T>,
    selectedValue: T,
    modifier: Modifier = Modifier,
    onValueSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text("$label:")
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .pointerHoverIcon(PointerIcon.Hand),
            ) {
                Text(selectedValue.displayName)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier =
                    Modifier
                        .heightIn(max = 300.dp)
                        .wrapContentSize()
                        .pointerHoverIcon(PointerIcon.Hand),
            ) {
                options.forEach {
                    DropdownMenuItem(onClick = {
                        onValueSelected(it)
                        expanded = false
                    }) {
                        Text(it.displayName)
                    }
                }
            }
        }
    }
}
