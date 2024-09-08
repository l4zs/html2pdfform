package de.l4zs.html2pdfform.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class SuffixTransformation(private val suffix: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val result = text + AnnotatedString(suffix)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = offset
            override fun transformedToOriginal(offset: Int): Int = minOf(offset, text.length)
        }
        return TransformedText(result, offsetMapping)
    }
}
