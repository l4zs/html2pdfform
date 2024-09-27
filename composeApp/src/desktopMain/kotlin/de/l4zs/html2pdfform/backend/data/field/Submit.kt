package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.extension.baseFont
import de.l4zs.html2pdfform.backend.extension.defaultRectangle
import de.l4zs.html2pdfform.backend.util.Actions
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

    override val value: String =
        super.value ?: run {
            context.logger.info("Value des Reset-Buttons fehlt, Standardwert 'Abschicken' wird stattdessen genommen")
            "Abschicken"
        }

    init {
        if (to.isBlank()) {
            context.logger.info("Fehlender Empfänger beim Submit Button (${element.id()})")
        }
        if (subject.isBlank()) {
            context.logger.info("Fehlender Betreff beim Submit Button (${element.id()})")
        }

        rectangle = element.defaultRectangle(context.config)
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
        context.acroForm.setButtonParams(field, PdfFormField.FF_PUSHBUTTON, mappingName, value)
    }

    override fun write() {
        applyWidget()
        setAdditionalActions()
        setDefaults()

        context.acroForm.drawButton(
            field,
            value,
            context.config.baseFont,
            context.config.fontSize,
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
