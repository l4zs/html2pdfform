package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.util.Actions
import org.jsoup.nodes.Element

class Url(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.URL) {
    init {
        if (pattern != null && patternMessage == null) {
            patternMessage = "Bitte geben Sie eine URL ein."
        }
        if (pattern == null) {
            pattern = "(http(s)?:\\/\\/)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-z]{2,6}([-a-zA-Z0-9@:%_\\+.~#?&\\/=]*)"
            patternMessage = "Bitte geben Sie eine URL ein."
        }
    }
}

fun url(
    element: Element,
    context: Context,
): FieldWithLabel<Url> {
    val url = Url(element, context)
    return FieldWithLabel(url, url.label(), context)
}
