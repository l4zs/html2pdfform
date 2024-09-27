package de.l4zs.html2pdfform.backend.data.field

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
    private val min: Int? = element.attr("min").toIntOrNull()
    private val max: Int? = element.attr("max").toIntOrNull()
    private val step: Int? = element.attr("step").toIntOrNull()
    private val base: Int? = min ?: value?.toIntOrNull() ?: step

    init {
        additionalActions[PdfFormField.AA_JS_KEY]!!.add(Actions.Number.keystrokeNumber)

        if (min != null) {
            additionalActions[PdfAnnotation.AA_JS_CHANGE]!!.add(Actions.Number.validateMin(min))
        }

        if (max != null) {
            additionalActions[PdfAnnotation.AA_JS_CHANGE]!!.add(Actions.Number.validateMax(max))
        }

        if (step != null) {
            additionalActions[PdfAnnotation.AA_JS_CHANGE]!!.add(Actions.Number.validateStep(step, base!!))
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
