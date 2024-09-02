package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.data.Rectangle
import org.jsoup.nodes.Element

abstract class FormField(
    val type: FieldType,
    val element: Element,
    open val context: Context,
    val id: Int,
) {
    val htmlId: String? = element.id().ifBlank { null }
    val name: String? = element.attr("name").ifBlank { null }
    open val value: String? = element.attr("value").ifBlank { null }
    val placeholder: String? = element.attr("placeholder").ifBlank { null }
    val toggles = if (element.hasAttr("toggles")) element.attr("toggles").split(" ").map { it.trim() } else emptyList()

    val required: Boolean = element.hasAttr("required")
    val readOnly: Boolean = element.hasAttr("readonly")
    val disabled: Boolean = element.hasAttr("disabled")
    val hidden: Boolean = element.hasAttr("hidden")

    val min: Int? = element.attr("min").toIntOrNull()
    val max: Int? = element.attr("max").toIntOrNull()
    val step: Int? = element.attr("step").toIntOrNull()

    lateinit var rectangle: Rectangle
    val width
        get() = rectangle.width
    val height
        get() = rectangle.height

    lateinit var field: PdfFormField

    val mappingName: String
        get() = htmlId ?: "${type.name.lowercase().replaceFirstChar { it.uppercase() }}-$id"

    open fun applyWidget() {
        field.setWidget(
            rectangle.toPdfRectangle(),
            PdfFormField.HIGHLIGHT_TOGGLE,
        )
    }

    open fun write() {
        applyWidget()
        field.setPage()

        if (readOnly || disabled) {
            field.setFieldFlags(PdfFormField.FF_READ_ONLY)
        }
        if (required) {
            field.setFieldFlags(PdfFormField.FF_REQUIRED)
        }
        field.setMappingName(mappingName)

        context.acroForm.addFormField(field)
    }
}
