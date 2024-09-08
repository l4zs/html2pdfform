package de.l4zs.html2pdfform.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp

@Composable
fun <T> DropdownSelector(
    label: String,
    options: List<Pair<T, String>>,
    selectedValue: T,
    modifier: Modifier = Modifier,
    onValueSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text("$label:")
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
                    .pointerHoverIcon(PointerIcon.Hand)
            ) {
                Text(options.firstOrNull { it.first == selectedValue }?.second ?: "Unknown")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .wrapContentSize()
                    .pointerHoverIcon(PointerIcon.Hand)
            ) {
                options.forEach { (value, name) ->
                    DropdownMenuItem(onClick = {
                        onValueSelected(value)
                        expanded = false
                    }) {
                        Text(name)
                    }
                }
            }
        }
    }
}
