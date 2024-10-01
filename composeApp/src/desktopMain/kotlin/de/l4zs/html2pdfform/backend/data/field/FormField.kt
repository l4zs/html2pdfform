package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.PdfName
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.data.Rectangle
import de.l4zs.html2pdfform.backend.extension.toPdfRectangle
import org.jsoup.nodes.Element

/**
 * Represents a form field. This class is abstract and should be extended
 * by the specific field types.
 *
 * @property type The type of the field
 * @property element The HTML element of the field
 * @property context The context of the field
 * @property id The ID of the field
 * @property htmlId The HTML ID of the field
 * @property name The name of the field
 * @property value The value of the field
 * @property toggles The toggles of the field
 * @property required Whether the field is required
 * @property readOnly Whether the field is read-only
 * @property disabled Whether the field is disabled
 * @property rectangle The rectangle of the field
 * @property width The width of the field
 * @property height The height of the field
 * @property fieldFlags The field flags of the field
 * @property flags The flags of the field
 * @property additionalActions The additional actions of the field
 * @property field The PDF form field of the field
 * @property mappingName The mapping name of the field used to identify the
 *    field in PDF
 */
abstract class FormField(
    val type: FieldType,
    val element: Element,
    open val context: Context,
    val id: Int,
) {
    private val htmlId: String? = element.id().ifBlank { null }
    val name: String? = element.attr("name").ifBlank { null }
    open val value: String? = element.attr("value").ifBlank { element.text().ifBlank { null } }
    val toggles = if (element.hasAttr("toggles")) element.attr("toggles").split(" ").map { it.trim() } else emptyList()

    val required: Boolean = element.hasAttr("required")
    val readOnly: Boolean = element.hasAttr("readonly")
    val disabled: Boolean = element.hasAttr("disabled")

    lateinit var rectangle: Rectangle
    val width
        get() = rectangle.width
    val height
        get() = rectangle.height

    var fieldFlags = 0
    var flags = PdfFormField.FLAGS_PRINT

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

    lateinit var field: PdfFormField

    val mappingName: String
        get() = htmlId ?: "${type.name.lowercase().replaceFirstChar { it.uppercase() }}-$id"

    /**
     * Applies the widget to the field.
     */
    fun applyWidget() {
        field.setWidget(
            rectangle.toPdfRectangle(),
            PdfFormField.HIGHLIGHT_TOGGLE,
        )
    }

    /**
     * Sets the additional actions of the field.
     */
    fun setAdditionalActions() {
        for (action in additionalActions.keys) {
            val code = additionalActions[action]
            if (code.isNullOrEmpty()) {
                continue
            }
            field.setAdditionalActions(action, PdfAction.javaScript(code.joinToString("\n"), context.writer))
        }
    }

    /**
     * Sets the default values of the field.
     */
    fun setDefaults() {
        field.setPage()
        if (value != null) {
            field.setDefaultValueAsString(value)
        }
        flags = flags or PdfFormField.FLAGS_PRINT

        if (readOnly || disabled) {
            fieldFlags = fieldFlags or PdfFormField.FF_READ_ONLY
        }
        if (required) {
            fieldFlags = fieldFlags or PdfFormField.FF_REQUIRED
        }
        field.setFlags(flags)
        field.setFieldFlags(fieldFlags)
        field.setMappingName(mappingName)
    }

    /**
     * Writes the field to the PDF.
     */
    open fun write() {
        applyWidget()
        setAdditionalActions()
        setDefaults()

        context.acroForm.addFormField(field)
    }
}
