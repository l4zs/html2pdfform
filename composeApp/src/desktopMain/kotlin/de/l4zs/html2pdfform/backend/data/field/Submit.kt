package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.extension.baseFont
import de.l4zs.html2pdfform.backend.extension.defaultRectangle
import de.l4zs.html2pdfform.backend.util.Actions
import de.l4zs.html2pdfform.resources.*
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_submit_default
import de.l4zs.html2pdfform.resources.converter_submit_default_log
import de.l4zs.html2pdfform.resources.converter_submit_missing_to
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

class Submit(
    element: Element,
    context: Context,
    default: String,
    defaultLog: String,
    missingTo: String,
    missingSubject: String,
    missingBody: String,
    id: Int = context.currentElementIndex,
) : FormField(FieldType.SUBMIT, element, context, id) {
    private val to = element.attr("to")
    private val cc = element.attr("cc")
    private val subject = element.attr("subject")
    private val body = element.attr("body")

    override val value: String =
        super.value ?: run {
            context.logger.info(defaultLog)
            default
        }

    init {
        if (to.isBlank()) {
            context.logger.info(missingTo)
        }
        if (subject.isBlank()) {
            context.logger.info(missingSubject)
        }
        if (body.isBlank()) {
            context.logger.info(missingBody)
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

suspend fun submit(
    element: Element,
    context: Context,
): FieldWithLabel<Submit> {
    val default = getString(Res.string.converter_submit_default)
    val field =
        Submit(
            element,
            context,
            default,
            getString(Res.string.converter_submit_default_log, default),
            getString(Res.string.converter_submit_missing_to, element.id()),
            getString(Res.string.converter_submit_missing_subject, element.id()),
            getString(Res.string.converter_submit_missing_body, element.id()),
        )
    val fieldWithLabel = FieldWithLabel(field, field.label(), context)
    return fieldWithLabel
}
