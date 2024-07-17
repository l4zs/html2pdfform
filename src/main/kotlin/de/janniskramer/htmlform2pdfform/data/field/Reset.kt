package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.config
import de.janniskramer.htmlform2pdfform.data.Actions
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.extensions.findLabel
import org.jsoup.nodes.Element

class Reset(
    element: Element,
    id: Int,
    private val title: String? = null,
) : FormField(FieldType.RESET, element, id) {
    override fun write(context: Context): PdfFormField {
        val rectangle = getDefaultRectangle(context)

        val field =
            context.acroForm.addResetButton(
                name ?: mappingName,
                title ?: value ?: "Reset",
                value ?: "Reset",
                config.baseFont,
                config.fontSize,
                rectangle.llx,
                rectangle.lly,
                rectangle.urx,
                rectangle.ury,
            )

        field.setMappingName(mappingName)

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

        return field
    }
}

fun reset(
    element: Element,
    context: Context,
): Reset {
    val label = element.findLabel()
    if (label != null) {
        return Reset(element, context.currentElementIndex, label.text())
    }
    return Reset(element, context.currentElementIndex)
}
