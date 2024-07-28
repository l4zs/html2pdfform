package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.extensions.findOptions
import org.jsoup.nodes.Element

data class Option(
    val value: String,
    val text: String,
)

class Select(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.SELECT) {
    private val multiple = element.hasAttr("multiple")
    private val size = element.attr("size").toIntOrNull() ?: 0
    private val options =
        element.findOptions().map {
            Option(
                it.attr("value") ?: it.text(),
                it.text(),
            )
        }
    private val editable = element.hasAttr("editable")
    private val sorted = element.hasAttr("sorted")

    private val isList = multiple && size > 1

    init {
        val text = base()

        if (sorted) {
            options.sortedBy { it.text }
        } else {
            options
        }.let { opt ->
            text.choices = opt.map { it.text }.toTypedArray()
            text.choiceExports = opt.map { it.value }.toTypedArray()
        }

        field =
            if (isList) {
                text.listField // list
            } else {
                text.comboField // dropdown
            }

        if (multiple) {
            field.setFieldFlags(PdfFormField.FF_MULTISELECT) // allow multiple selections
        }
        if (editable && !isList) {
            field.setFieldFlags(PdfFormField.FF_EDIT) // allow custom text in dropdown
        }

        field.addTextActions()
    }
}

fun select(
    element: Element,
    context: Context,
): FieldWithLabel<Select> {
    val select = Select(element, context)
    return FieldWithLabel(select, select.label(), context)
}
