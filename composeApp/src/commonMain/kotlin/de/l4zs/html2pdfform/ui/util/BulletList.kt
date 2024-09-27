package de.l4zs.html2pdfform.ui.util

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.buildAnnotatedString

@Composable
fun BulletList(
    items: List<*>,
    level: Int = 0,
) {
    val bullet =
        when (level) {
            0 -> "\u2022"
            1 -> "\u25E6"
            else -> "\u25AA"
        }
    val indent = "    ".repeat(level)

    Column {
        items.forEach { item ->
            when (item) {
                is String -> {
                    Text("$indent$bullet $item")
                }

                is List<*> -> {
                    BulletList(item, level = level + 1)
                }

                else -> {
                    Text(
                        text =
                            buildAnnotatedString {
                                append("$indent$bullet $item")
                            },
                    )
                }
            }
        }
    }
}
