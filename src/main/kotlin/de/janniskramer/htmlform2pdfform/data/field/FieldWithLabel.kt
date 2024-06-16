package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.Config
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.extensions.height
import kotlin.math.max

data class FieldWithLabel<T : FormField>(
    val field: T,
    val label: Label?,
) : FormField(FieldType.FIELD_WITH_LABEL, field.element, field.id) {
    override fun write(context: Context): PdfFormField =
        when (field.type) {
            FieldType.CHECKBOX, FieldType.RADIO -> writeHorizontal(context)
            else -> writeVertical(context)
        }

    private fun writeVertical(context: Context): PdfFormField {
        val height = (label?.element?.height() ?: 0f) + Config.innerPaddingY + field.element.height()

        if (!context.locationHandler.wouldFitOnPageY(height)) {
            context.locationHandler.newPage()
        }

        if (label == null) {
            return field.write(context)
        }

        label.write(context)

        context.locationHandler.newLine()
        context.locationHandler.padY(Config.innerPaddingY)

        return field.write(context)
    }

    private fun writeHorizontal(context: Context): PdfFormField {
        val height = max(field.element.height(), label?.element?.height() ?: 0f)

        if (!context.locationHandler.wouldFitOnPageY(height)) {
            context.locationHandler.newPage()
        }

        val f = field.write(context)

        if (label == null) {
            return f
        }

        context.locationHandler.padX(Config.innerPaddingX)

        label.write(context)

        return f
    }
}
