package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.Config
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.extensions.height
import de.janniskramer.htmlform2pdfform.extensions.width
import kotlin.math.max

data class FieldWithLabel<T : FormField>(
    val formField: T,
    val label: Label?,
) : FormField(FieldType.FIELD_WITH_LABEL, formField.element, formField.id) {
    val width: Float
        get() {
            when (formField.type) {
                FieldType.CHECKBOX, FieldType.RADIO -> {
                    val labelWidth = label?.element?.width() ?: 0f
                    return formField.element.width() + Config.innerPaddingX + labelWidth
                }
                else -> return max(formField.element.width(), label?.element?.width() ?: 0f)
            }
        }

    override fun write(context: Context): PdfFormField =
        when (formField.type) {
            FieldType.CHECKBOX, FieldType.RADIO -> writeHorizontal(context)
            else -> writeVertical(context)
        }

    private fun writeVertical(context: Context): PdfFormField {
        val height = (label?.element?.height() ?: 0f) + Config.innerPaddingY + formField.element.height()

        if (!context.locationHandler.wouldFitOnPageY(height)) {
            context.locationHandler.newPage()
        }

        if (label == null) {
            return formField.write(context)
        }

        label.write(context)

        context.locationHandler.newLine()
        context.locationHandler.padY(Config.innerPaddingY)

        return formField.write(context)
    }

    private fun writeHorizontal(context: Context): PdfFormField {
        val height = max(formField.element.height(), label?.element?.height() ?: 0f)

        if (!context.locationHandler.wouldFitOnPageY(height)) {
            context.locationHandler.newPage()
        }

        val f = formField.write(context)

        if (label == null) {
            return f
        }

        context.locationHandler.padX(Config.innerPaddingX)

        label.write(context)

        return f
    }
}
