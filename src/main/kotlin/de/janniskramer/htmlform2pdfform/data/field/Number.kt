package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Actions
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Number(
    element: Element,
    id: Int,
) : Text(element, id, FieldType.NUMBER) {
    private val min: Int? = element.attr("min").toIntOrNull()
    private val max: Int? = element.attr("max").toIntOrNull()
    private val step: Int? = element.attr("step").toIntOrNull()

    override fun write(context: Context): PdfFormField {
        val field = super.convert(context)

        field.setAdditionalActions(
            PdfFormField.AA_JS_KEY,
            PdfAction.javaScript(
                Actions.Number.keystrokeNumber,
                context.writer,
            ),
        )

        if (min != null) {
            field.setAdditionalActions(
                PdfFormField.AA_JS_CHANGE,
                PdfAction.javaScript(
                    Actions.Number.validateMin(min),
                    context.writer,
                ),
            )
        }

        if (max != null) {
            field.setAdditionalActions(
                PdfFormField.AA_JS_CHANGE,
                PdfAction.javaScript(
                    Actions.Number.validateMax(max),
                    context.writer,
                ),
            )
        }

        if (step != null) {
            field.setAdditionalActions(
                PdfFormField.AA_JS_CHANGE,
                PdfAction.javaScript(
                    Actions.Number.validateStep(step, min ?: value?.toIntOrNull() ?: step),
                    context.writer,
                ),
            )
        }

        context.acroForm.addFormField(field)

        return field
    }
}

fun FormFields.number(
    element: Element,
    context: Context,
): FieldWithLabel<Number> {
    val number = Number(element, context.currentElementIndex)
    return FieldWithLabel(number, number.label(context))
}
