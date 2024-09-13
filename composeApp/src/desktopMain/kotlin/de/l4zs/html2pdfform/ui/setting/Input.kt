package de.l4zs.html2pdfform.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import de.l4zs.html2pdfform.ui.SuffixTransformation
import de.l4zs.html2pdfform.util.centimeterToPoint

private fun String.filterNumberAndComma(): String = filter { it.isDigit() || it == '.' || it == ',' }

private fun String.filterNumber(): String = filter { it.isDigit() }

fun String.centimeterInputToPoint(): Float {
    val filteredValue = filterNumberAndComma().toFloatOrNull() ?: 0f
    return filteredValue.centimeterToPoint()
}

@Composable
fun FloatInput(
    label: String,
    value: Float,
    default: Float = 0f,
    suffix: String? = null,
    onValueChange: (Float) -> Unit,
) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { onValueChange(it.filterNumberAndComma().toFloatOrNull() ?: default) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        maxLines = 1,
        visualTransformation = suffix?.let { SuffixTransformation(it) } ?: VisualTransformation.None,
    )
}

@Composable
fun IntInput(
    label: String,
    value: Int,
    default: Int = 0,
    onValueChange: (Int) -> Unit,
) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { onValueChange(it.filterNumber().toIntOrNull() ?: default) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        maxLines = 1,
    )
}

@Composable
fun Input(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 1,
    )
}

@Composable
fun Checkbox(
    label: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        androidx.compose.material.Checkbox(
            checked = value,
            onCheckedChange = { onValueChange(it) },
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.clickable { onValueChange(!value) },
        )
    }
}
