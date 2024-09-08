package de.janniskramer.htmlform2pdfform.data.field

import de.janniskramer.htmlform2pdfform.config
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.data.Rectangle
import kotlin.math.max

data class FieldWithLabel<T : FormField>(
    val formField: T,
    val label: Label?,
    override val context: Context,
) : FormField(FieldType.FIELD_WITH_LABEL, formField.element, context, context.currentElementIndex) {
    init {
        rectangle =
            when (formField.type) {
                FieldType.CHECKBOX, FieldType.RADIO -> {
                    Rectangle(
                        formField.width + config.innerPaddingX + (label?.width ?: -config.innerPaddingX),
                        max(formField.height, label?.height ?: 0f),
                    )
                }

                else -> {
                    Rectangle(
                        max(formField.width, label?.width ?: 0f),
                        formField.height + config.innerPaddingY + (label?.height ?: -config.innerPaddingY),
                    )
                }
            }
    }

    override fun write() =
        when (formField.type) {
            FieldType.CHECKBOX, FieldType.RADIO -> writeHorizontal()
            else -> writeVertical()
        }

    private fun writeHorizontal() {
        formField.rectangle =
            formField.rectangle.move(
                rectangle.llx,
                rectangle.lly + (rectangle.height - formField.height) / 2,
            )
        formField.write()

        if (label == null) {
            return
        }

        label.rectangle =
            label.rectangle.move(
                formField.rectangle.urx + config.innerPaddingX,
                rectangle.lly + (rectangle.height - label.height) / 2,
            )
        label.write()
    }

    private fun writeVertical() {
        formField.rectangle =
            formField.rectangle.move(
                rectangle.llx,
                rectangle.lly,
            )
        if (label == null) {
            formField.write()
            return
        }

        label.rectangle =
            label.rectangle.move(
                rectangle.llx,
                rectangle.lly + (rectangle.height - label.height),
            )
        label.write()
        formField.write()
    }
}
