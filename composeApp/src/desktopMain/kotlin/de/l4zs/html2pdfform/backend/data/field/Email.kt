package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_email_pattern_message
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

/**
 * Represents an email field.
 *
 * @param element The HTML element that represents the email field.
 * @param context The context of the form.
 * @param defaultPatternMessage The default pattern message.
 * @param id The ID of the field.
 */
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

/**
 * Creates an email form field. If a label is present, it will be used as
 * the field label.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The email form field with label.
 */
suspend fun email(
    element: Element,
    context: Context,
): FieldWithLabel<Email> {
    // use default pattern message from resources
    val email = Email(element, context, getString(Res.string.converter_email_pattern_message))
    return FieldWithLabel(email, email.label(), context)
}
