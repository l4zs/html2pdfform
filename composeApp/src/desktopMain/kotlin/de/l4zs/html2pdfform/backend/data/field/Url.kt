package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_url_pattern_message
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

class Url(
    element: Element,
    context: Context,
    defaultPatternMessage: String,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.URL) {
    init {
        if (pattern != null && patternMessage == null) {
            patternMessage = defaultPatternMessage
        }
        if (pattern == null) {
            pattern = "(http(s)?:\\/\\/)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-z]{2,6}([-a-zA-Z0-9@:%_\\+.~#?&\\/=]*)"
            patternMessage = defaultPatternMessage
        }
    }
}

suspend fun url(
    element: Element,
    context: Context,
): FieldWithLabel<Url> {
    val url = Url(element, context, getString(Res.string.converter_url_pattern_message))
    return FieldWithLabel(url, url.label(), context)
}
