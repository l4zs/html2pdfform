package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.util.Actions
import org.jsoup.nodes.Element

class Email(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.EMAIL) {
    init {
        if (pattern != null && patternMessage == null) {
            patternMessage = "Bitte geben Sie eine E-Mail-Adresse ein."
        }
        if (pattern == null) {
            pattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
            patternMessage = "Bitte geben Sie eine E-Mail-Adresse ein."
        }
    }
}

fun email(
    element: Element,
    context: Context,
): FieldWithLabel<Email> {
    val email = Email(element, context)
    return FieldWithLabel(email, email.label(), context)
}
