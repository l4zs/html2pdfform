package de.l4zs.html2pdfform.ui.util

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
import de.l4zs.html2pdfform.backend.data.Translatable
import org.jetbrains.compose.resources.stringResource

/**
 * A dropdown selector for selecting a value from a list of options.
 * The options should be of type [Translatable] to display the translated string.
 *
 * @param label The label of the dropdown.
 * @param options The list of options to select from.
 * @param selectedValue The currently selected value.
 * @param modifier The modifier for the dropdown.
 * @param onValueSelected The callback for when a value is selected.
 */
@Composable
fun <T : Translatable> DropdownSelector(
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
                Text(stringResource(selectedValue.resource))
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
                        Text(stringResource(it.resource))
                    }
                }
            }
        }
    }
}
