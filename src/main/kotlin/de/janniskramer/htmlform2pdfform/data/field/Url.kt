package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Actions
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Url(
    element: Element,
    id: Int,
) : Text(element, id, FieldType.URL) {
    override fun write(context: Context): PdfFormField {
        val field = super.convert(context)

        field.setAdditionalActions(
            PdfFormField.AA_JS_CHANGE,
            PdfAction.javaScript(
                Actions.Text.validatePattern("https?://.+"),
                context.writer,
            ),
        )

        context.acroForm.addFormField(field)

        return field
    }
}

fun url(
    element: Element,
    context: Context,
): FieldWithLabel<Url> {
    val url = Url(element, context.currentElementIndex)
    return FieldWithLabel(url, url.label(context))
}
