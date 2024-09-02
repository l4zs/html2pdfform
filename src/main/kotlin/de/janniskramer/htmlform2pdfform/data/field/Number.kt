package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.util.Actions
import org.jsoup.nodes.Element

class Number(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.NUMBER) {
    init {
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
    }
}

fun number(
    element: Element,
    context: Context,
): FieldWithLabel<Number> {
    val number = Number(element, context)
    return FieldWithLabel(number, number.label(), context)
}
