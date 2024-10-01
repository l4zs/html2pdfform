package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_url_pattern_message
import io.ktor.client.engine.ProxyBuilder.http
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

/**
 * Represents a URL field.
 *
 * @param element The HTML element that represents the URL field.
 * @param context The context of the form.
 * @param defaultPatternMessage The default pattern message for the URL field.
 * @param id The ID of the URL field.
 */
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

/**
 * Creates a url form field. If a label is present, it will be used as
 * the field label.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The url form field with label.
 */
suspend fun url(
    element: Element,
    context: Context,
): FieldWithLabel<Url> {
    // use default pattern message from resources
    val url = Url(element, context, getString(Res.string.converter_url_pattern_message))
    return FieldWithLabel(url, url.label(), context)
}
