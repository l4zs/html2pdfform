package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfAnnotation
import com.lowagie.text.pdf.PdfAppearance
import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.extension.baseFont
import de.l4zs.html2pdfform.backend.extension.defaultRectangle
import de.l4zs.html2pdfform.backend.util.Actions
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_reset_default
import de.l4zs.html2pdfform.resources.converter_reset_default_log
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

class Reset(
    element: Element,
    context: Context,
    defaultLabel: String,
    defaultLabelLog: String,
    id: Int = context.currentElementIndex,
) : FormField(FieldType.RESET, element, context, id) {
    override val value: String =
        super.value ?: run {
            context.logger.info(defaultLabelLog)
            defaultLabel
        }

    init {
        rectangle = element.defaultRectangle(context.config)

        val action = PdfAction.createResetForm(null, 0)
        field =
            PdfFormField(
                context.writer,
                rectangle.llx,
                rectangle.lly,
                rectangle.urx,
                rectangle.ury,
                action,
            )
        context.acroForm.setButtonParams(
            field,
            PdfFormField.FF_PUSHBUTTON,
            mappingName,
            value,
        )

        additionalActions[PdfFormField.AA_DOWN]!!.add(Actions.Reset.buttonDown)
        // use blur instead of up to prevent errors when mouse is released outside the button
        additionalActions[PdfFormField.AA_BLUR]!!.add(Actions.Reset.buttonUp)
    }

    override fun write() {
        applyWidget()
        setAdditionalActions()
        setDefaults()

        val pa = PdfAppearance.createAppearance(context.writer, rectangle.width, rectangle.height)
        pa.drawButton(
            0.0f,
            0.0f,
            rectangle.width,
            rectangle.height,
            value,
            context.config.baseFont,
            context.config.fontSize,
        )
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, pa)
        field.setAppearance(PdfAnnotation.APPEARANCE_DOWN, pa)
        field.setAppearance(PdfAnnotation.APPEARANCE_ROLLOVER, pa)

        context.acroForm.addFormField(field)
    }
}

suspend fun reset(
    element: Element,
    context: Context,
): FieldWithLabel<Reset> {
    val default = getString(Res.string.converter_reset_default)
    val field =
        Reset(
            element,
            context,
            default,
            getString(Res.string.converter_reset_default_log, default),
        )
    val fieldWithLabel = FieldWithLabel(field, field.label(), context)
    return fieldWithLabel
}
