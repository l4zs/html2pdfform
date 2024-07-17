package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.data.Rectangle
import de.janniskramer.htmlform2pdfform.extensions.capitalize
import org.jsoup.nodes.Element

abstract class FormField(
    val type: FieldType,
    val element: Element,
    val id: Int,
    val context: Context,
) {
    val htmlId: String? = element.id().ifBlank { null }
    val name: String? = element.attr("name").ifBlank { null }
    open val value: String? = element.attr("value").ifBlank { null }
    val placeholder: String? = element.attr("placeholder").ifBlank { null }
    val toggles = if (element.hasAttr("toggles")) element.attr("toggles").split(",") else emptyList()

    val required: Boolean = element.hasAttr("required")
    val readOnly: Boolean = element.hasAttr("readonly")
    val disabled: Boolean = element.hasAttr("disabled")
    val hidden: Boolean = element.hasAttr("hidden")

    lateinit var rectangle: Rectangle
    val width = rectangle.width
    val height = rectangle.height

    lateinit var field: PdfFormField

    val mappingName: String
        get() = htmlId ?: "${type.name.capitalize()}-$id"

    open fun applyWidget() {
        field.setWidget(
            com.lowagie.text.Rectangle(
                rectangle.llx,
                rectangle.lly,
                rectangle.urx,
                rectangle.ury,
            ),
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
