package de.l4zs.html2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.data.Context
import de.l4zs.html2pdfform.util.Actions
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

        if (
            min != null ||
            max != null ||
            step != null
        ) {
            field.setAdditionalActions(
                PdfFormField.AA_JS_CHANGE,
                PdfAction.javaScript(
                    Actions.Number.validateMinMaxStep(min, max, step, min ?: value?.toIntOrNull() ?: step),
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
