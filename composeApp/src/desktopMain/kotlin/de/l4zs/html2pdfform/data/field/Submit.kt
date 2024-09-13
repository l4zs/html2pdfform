package de.l4zs.html2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.config.baseFont
import de.l4zs.html2pdfform.config.config
import de.l4zs.html2pdfform.data.Context
import de.l4zs.html2pdfform.extension.defaultRectangle
import de.l4zs.html2pdfform.util.Actions
import org.jsoup.nodes.Element

class Submit(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : FormField(FieldType.SUBMIT, element, context, id) {
    private val to = element.attr("to")
    private val cc = element.attr("cc")
    private val subject = element.attr("subject")
    private val body = element.attr("body")

    init {
        if (to.isBlank()) {
            context.logger.info("Fehlender Empfänger beim Submit Button")
        }

        rectangle = element.defaultRectangle()
        val action =
            PdfAction.javaScript(
                Actions.Submit.submitMail(
                    to,
                    cc,
                    subject,
                    body,
                ),
                context.writer,
            )

        field =
            PdfFormField(
                context.writer,
                rectangle.lly,
                rectangle.urx,
                rectangle.llx,
                rectangle.ury,
                action,
            )
        if (value == null) {
            context.logger.info("Value des Submit-Buttons fehlt, Standardwert 'Abschicken' wird stattdessen genommen")
        }
        context.acroForm.setButtonParams(field, PdfFormField.FF_PUSHBUTTON, name ?: mappingName, value ?: "Abschicken")
    }

    override fun write() {
        super.applyWidget()
        field.setPage()

        if (readOnly || disabled) {
            field.setFieldFlags(PdfFormField.FF_READ_ONLY)
        }
        if (required) {
            field.setFieldFlags(PdfFormField.FF_REQUIRED)
        }
        field.setMappingName(mappingName)

        context.acroForm.drawButton(
            field,
            value ?: "Abschicken",
            config.baseFont,
            config.fontSize,
            rectangle.llx,
            rectangle.lly,
            rectangle.urx,
            rectangle.ury,
        )

        context.acroForm.addFormField(field)
    }
}

fun submit(
    element: Element,
    context: Context,
): FieldWithLabel<Submit> {
    val field = Submit(element, context)
    val fieldWithLabel = FieldWithLabel(field, field.label(), context)
    return fieldWithLabel
}
