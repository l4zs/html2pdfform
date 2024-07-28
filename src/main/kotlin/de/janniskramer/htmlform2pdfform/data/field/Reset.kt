package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.config
import de.janniskramer.htmlform2pdfform.data.Actions
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.extensions.defaultRectangle
import de.janniskramer.htmlform2pdfform.extensions.findLabel
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
        field = PdfFormField.createPushButton(context.writer)
        field.setAction(action)
        field.setFlags(PdfFormField.FLAGS_PRINT)
        field.setFieldName(name ?: mappingName)
        field.setValueAsString(value ?: "Reset")

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

        context.acroForm.drawButton(
            field,
            title ?: value ?: "Reset",
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
