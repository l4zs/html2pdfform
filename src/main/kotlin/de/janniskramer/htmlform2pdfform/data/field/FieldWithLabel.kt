package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.Config
import de.janniskramer.htmlform2pdfform.data.Context

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
        if (label == null) {
            return field.write(context)
        }

        label.write(context)

        context.locationHandler.newLine()
        context.locationHandler.padY(Config.innerPaddingY)

        return field.write(context)
    }

    private fun writeHorizontal(context: Context): PdfFormField {
        val f = field.write(context)

        if (label == null) {
            return f
        }

        context.locationHandler.padX(Config.innerPaddingX)

        label.write(context)

        return f
    }
}
