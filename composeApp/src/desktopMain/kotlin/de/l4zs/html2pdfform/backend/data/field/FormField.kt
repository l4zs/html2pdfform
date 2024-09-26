package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.PdfName
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.data.Rectangle
import de.l4zs.html2pdfform.backend.extension.toPdfRectangle
import org.jsoup.nodes.Element

abstract class FormField(
    val type: FieldType,
    val element: Element,
    open val context: Context,
    val id: Int,
) {
    val htmlId: String? = element.id().ifBlank { null }
    val name: String? = element.attr("name").ifBlank { null }
    open val value: String? = element.attr("value").ifBlank { element.text().ifBlank { null } }
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

    val additionalActions: MutableMap<PdfName, MutableList<String>> =
        mutableMapOf(
            PdfFormField.AA_JS_CHANGE to mutableListOf(),
            PdfFormField.AA_JS_KEY to mutableListOf(),
            PdfFormField.AA_JS_FORMAT to mutableListOf(),
            PdfFormField.AA_JS_OTHER_CHANGE to mutableListOf(),
            PdfFormField.AA_UP to mutableListOf(),
            PdfFormField.AA_DOWN to mutableListOf(),
            PdfFormField.AA_FOCUS to mutableListOf(),
            PdfFormField.AA_BLUR to mutableListOf(),
            PdfFormField.AA_ENTER to mutableListOf(),
            PdfFormField.AA_EXIT to mutableListOf(),
        )

    fun setAdditionalActions() {
        for (action in additionalActions.keys) {
            val code = additionalActions[action]
            println("$action -> $code")
            if (code.isNullOrEmpty()) {
                continue
            }
            field.setAdditionalActions(action, PdfAction.javaScript(code.joinToString("\n"), context.writer, true))
        }
    }

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
        setAdditionalActions()
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
