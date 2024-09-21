package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.data.Rectangle
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
                        formField.width + context.config.innerPaddingX + (
                            label?.width
                                ?: -context.config.innerPaddingX
                        ),
                        max(formField.height, label?.height ?: 0f),
                    )
                }

                else -> {
                    Rectangle(
                        max(formField.width, label?.width ?: 0f),
                        formField.height + context.config.innerPaddingY + (
                            label?.height
                                ?: -context.config.innerPaddingY
                        ),
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
                formField.rectangle.urx + context.config.innerPaddingX,
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
