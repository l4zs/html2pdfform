package de.l4zs.html2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfAnnotation
import com.lowagie.text.pdf.PdfAppearance
import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.ui.config
import de.l4zs.html2pdfform.data.Context
import de.l4zs.html2pdfform.extension.defaultRectangle
import de.l4zs.html2pdfform.extension.findLabel
import de.l4zs.html2pdfform.util.Actions
import org.jsoup.nodes.Element

class Reset(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
    val title: String? = null,
) : FormField(FieldType.RESET, element, context, id) {
    init {
        rectangle = element.defaultRectangle()

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
        context.acroForm.setButtonParams(field, PdfFormField.FF_PUSHBUTTON, name ?: mappingName, value ?: "Reset")

        field.setAdditionalActions(
            PdfFormField.AA_DOWN,
            PdfAction.javaScript(
                Actions.Reset.buttonDown,
                context.writer,
            ),
        )

        field.setAdditionalActions(
            PdfFormField.AA_BLUR, // use blur instead of up to prevent errors when mouse is released outside the button
            PdfAction.javaScript(
                Actions.Reset.buttonUp,
                context.writer,
            ),
        )
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

        val pa = PdfAppearance.createAppearance(context.writer, rectangle.width, rectangle.height)
        pa.drawButton(
            0.0f,
            0.0f,
            rectangle.width,
            rectangle.height,
            title ?: value ?: "Reset",
            config.baseFont,
            config.fontSize,
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
): Reset {
    val label = element.findLabel()
    if (label != null) {
        return Reset(element, context, title = label.text())
    }
    return Reset(element, context)
}
