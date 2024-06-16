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
    id: Int,
) : Text(element, id, FieldType.SELECT) {
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

    override fun write(context: Context): PdfFormField {
        val text = base(context)

        if (sorted) {
            val sorted = options.sortedBy { it.text }
            text.choices = sorted.map { it.text }.toTypedArray()
            text.choiceExports = sorted.map { it.value }.toTypedArray()
        } else {
            text.choices = options.map { it.text }.toTypedArray()
            text.choiceExports = options.map { it.value }.toTypedArray()
        }

        val field =
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

        field.addTextActions(context)

        context.acroForm.addFormField(field)

        return field
    }
}

fun FormFields.select(
    element: Element,
    context: Context,
): FieldWithLabel<Select> {
    val select = Select(element, context.currentElementIndex)
    return FieldWithLabel(select, select.label(context))
}
