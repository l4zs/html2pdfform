package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfAnnotation
import com.lowagie.text.pdf.PdfAppearance
import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.extension.baseFont
import de.l4zs.html2pdfform.backend.extension.defaultRectangle
import de.l4zs.html2pdfform.backend.extension.findLabel
import de.l4zs.html2pdfform.backend.util.Actions
import org.jsoup.nodes.Element

class Reset(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : FormField(FieldType.RESET, element, context, id) {
    override val value: String =
        super.value ?: run {
            context.logger.info("Value des Reset-Buttons fehlt, Standardwert 'Zurücksetzen' wird stattdessen genommen")
            "Zurücksetzen"
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

fun reset(
    element: Element,
    context: Context,
): FieldWithLabel<Reset> {
    val field = Reset(element, context)
    val fieldWithLabel = FieldWithLabel(field, field.label(), context)
    return fieldWithLabel
}
