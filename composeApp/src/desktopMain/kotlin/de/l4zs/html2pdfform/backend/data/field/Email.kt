package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_email_pattern_message
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

class Email(
    element: Element,
    context: Context,
    defaultPatternMessage: String,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.EMAIL) {
    init {
        if (pattern != null && patternMessage == null) {
            patternMessage = defaultPatternMessage
        }
        if (pattern == null) {
            pattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
            patternMessage = defaultPatternMessage
        }
    }
}

suspend fun email(
    element: Element,
    context: Context,
): FieldWithLabel<Email> {
    val email = Email(element, context, getString(Res.string.converter_email_pattern_message))
    return FieldWithLabel(email, email.label(), context)
}
