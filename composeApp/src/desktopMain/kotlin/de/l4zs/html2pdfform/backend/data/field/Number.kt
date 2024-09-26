package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfAnnotation
import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.util.Actions
import org.jsoup.nodes.Element

class Number(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.NUMBER) {
    init {
        additionalActions[PdfFormField.AA_JS_KEY]!!.add(Actions.Number.keystrokeNumber)

        if (
            min != null ||
            max != null ||
            step != null
        ) {
            additionalActions[PdfAnnotation.AA_JS_CHANGE]!!.add(
                Actions.Number.validateMinMaxStep(
                    min,
                    max,
                    step,
                    min ?: value?.toIntOrNull() ?: step,
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
