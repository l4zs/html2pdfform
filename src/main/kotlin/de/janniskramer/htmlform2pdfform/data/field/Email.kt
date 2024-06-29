package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Actions
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Email(
    element: Element,
    id: Int,
) : Text(element, id, FieldType.EMAIL) {
    override fun write(context: Context): PdfFormField {
        val field = super.convert(context)

        field.setAdditionalActions(
            PdfFormField.AA_JS_CHANGE,
            PdfAction.javaScript(
                Actions.Email.validateEmail,
                context.writer,
            ),
        )

        context.acroForm.addFormField(field)

        return field
    }
}

fun email(
    element: Element,
    context: Context,
): FieldWithLabel<Email> {
    val email = Email(element, context.currentElementIndex)
    return FieldWithLabel(email, email.label(context))
}
