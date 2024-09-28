package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfAnnotation
import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.util.Actions
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.action_number_max_message
import de.l4zs.html2pdfform.resources.action_number_min_message
import de.l4zs.html2pdfform.resources.action_number_step_message
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
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
            val message = runBlocking { getString(Res.string.action_number_min_message, min) }
            additionalActions[PdfAnnotation.AA_JS_CHANGE]!!.add(Actions.Number.validateMin(min, message))
        }

        if (max != null) {
            val message = runBlocking { getString(Res.string.action_number_max_message, max) }
            additionalActions[PdfAnnotation.AA_JS_CHANGE]!!.add(Actions.Number.validateMax(max, message))
        }

        if (step != null && base != null) {
            val message = runBlocking { getString(Res.string.action_number_step_message, step, base) }
            additionalActions[PdfAnnotation.AA_JS_CHANGE]!!.add(Actions.Number.validateStep(step, base, message))
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
