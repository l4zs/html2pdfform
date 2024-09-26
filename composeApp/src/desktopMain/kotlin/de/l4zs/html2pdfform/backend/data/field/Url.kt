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
        additionalActions[PdfFormField.AA_JS_CHANGE]!!.add(Actions.Text.validatePattern("https?://.+"))
    }
}

fun url(
    element: Element,
    context: Context,
): FieldWithLabel<Url> {
    val url = Url(element, context)
    return FieldWithLabel(url, url.label(), context)
}
