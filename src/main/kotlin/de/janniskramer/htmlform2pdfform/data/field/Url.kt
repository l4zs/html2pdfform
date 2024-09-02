package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.util.Actions
import org.jsoup.nodes.Element

class Url(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.URL) {
    init {
        field.setAdditionalActions(
            PdfFormField.AA_JS_CHANGE,
            PdfAction.javaScript(
                Actions.Text.validatePattern("https?://.+"),
                context.writer,
            ),
        )
    }
}

fun url(
    element: Element,
    context: Context,
): FieldWithLabel<Url> {
    val url = Url(element, context)
    return FieldWithLabel(url, url.label(), context)
}
