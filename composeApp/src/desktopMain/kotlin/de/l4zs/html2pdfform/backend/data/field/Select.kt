package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.extension.findOptions
import de.l4zs.html2pdfform.backend.extension.toPdfRectangle
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_select_override
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

class Select(
    element: Element,
    context: Context,
    overrideLog: String,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.SELECT) {
    data class Option(
        val value: String,
        val text: String,
        val selected: Boolean = false,
    )

    private val multiple = element.hasAttr("multiple")
    private val editable = element.hasAttr("editable")
    private val sorted = element.hasAttr("sorted")
    private val options =
        element
            .findOptions()
            .map {
                Option(
                    it.attr("value") ?: it.text(),
                    it.text(),
                    it.hasAttr("selected"),
                )
            }.let {
                if (sorted) {
                    it.sortedBy(Option::text)
                } else {
                    it
                }
            }
    private val selectedOptions =
        options.mapIndexed { index, option -> index to option.selected }.filter { it.second }.map { it.first }
    override val value: String? = element.attr("value")

    init {
        val text = base()

        text.choices = options.map { it.text }.toTypedArray()
        text.choiceExports = options.map { it.value }.toTypedArray()

        if (multiple) {
            // allow multiple selections
            fieldFlags = fieldFlags or PdfFormField.FF_MULTISELECT
            if (editable) {
                context.logger.info(overrideLog)
            }
            text.options = PdfFormField.FF_MULTISELECT
            text.setChoiceSelections(selectedOptions)
            field = text.listField // list
        } else {
            if (editable) {
                // allow custom text in dropdown
                fieldFlags = fieldFlags or PdfFormField.FF_EDIT
                text.options = PdfFormField.FF_EDIT
            }
            text.choiceSelection = selectedOptions.firstOrNull() ?: 0
            field = text.comboField // dropdown
        }
    }

    override fun write() {
        field.setWidget(
            rectangle.toPdfRectangle(),
            PdfFormField.HIGHLIGHT_INVERT,
        )

        if (readOnly || disabled) {
            fieldFlags = fieldFlags or PdfFormField.FF_READ_ONLY
        }
        if (required) {
            fieldFlags = fieldFlags or PdfFormField.FF_REQUIRED
        }
        field.setMappingName(mappingName)

        context.acroForm.addFormField(field)
    }
}

suspend fun select(
    element: Element,
    context: Context,
): FieldWithLabel<Select> {
    val select = Select(element, context, getString(Res.string.converter_select_override, element.id()))
    return FieldWithLabel(select, select.label(), context)
}
