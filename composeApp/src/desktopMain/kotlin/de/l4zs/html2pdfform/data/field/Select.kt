package de.l4zs.html2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.data.Context
import de.l4zs.html2pdfform.extension.findOptions
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
    private val options =
        element.findOptions().map {
            Option(
                it.attr("value") ?: it.text(),
                it.text(),
            )
        }
    private val editable = element.hasAttr("editable")
    private val sorted = element.hasAttr("sorted")

    private val isList = multiple

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
        } else if (editable) {
            context.logger.info("Select kann nur im Dropdown (multiple=false) editiert werden")
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
