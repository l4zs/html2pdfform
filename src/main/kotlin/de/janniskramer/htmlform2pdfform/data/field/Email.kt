package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Actions
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Email(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.EMAIL) {
    init {
        field.setAdditionalActions(
            PdfFormField.AA_JS_CHANGE,
            PdfAction.javaScript(
                Actions.Email.validateEmail,
                context.writer,
            ),
        )
    }
}

fun email(
    element: Element,
    context: Context,
): FieldWithLabel<Email> {
    val email = Email(element, context)
    return FieldWithLabel(email, email.label(), context)
}
