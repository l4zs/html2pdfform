package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.extension.defaultRectangle
import de.l4zs.html2pdfform.backend.util.Actions
import org.jsoup.nodes.Element

/**
 * Represents a checkbox form field.
 *
 * @param element The HTML element.
 * @param context The context.
 * @param id The field ID.
 * @property checked Whether the checkbox is checked.
 * @property value The value of the checkbox.
 */
class Checkbox(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : FormField(FieldType.CHECKBOX, element, context, id) {
    private val checked = this.element.hasAttr("checked")
    override val value: String = if (checked) super.value ?: "$id" else "Off"

    init {
        rectangle = element.defaultRectangle(context.config)
        field = PdfFormField.createCheckBox(context.writer)
        context.acroForm.setCheckBoxParams(
            field,
            mappingName,
            value,
            checked,
            rectangle.llx,
            rectangle.lly,
            rectangle.urx,
            rectangle.ury,
        )

        if (toggles.isNotEmpty()) {
            additionalActions[PdfFormField.AA_JS_CHANGE]!!.add(Actions.Checkbox.toggleFields(toggles))
        }
    }

    override fun write() {
        applyWidget()
        setAdditionalActions()
        setDefaults()

        context.acroForm.drawCheckBoxAppearences(
            field,
            value,
            rectangle.llx,
            rectangle.lly,
            rectangle.urx,
            rectangle.ury,
        )

        context.acroForm.addFormField(field)
    }
}

/**
 * Creates a checkbox form field. If a label is present, it will be used as
 * the field label.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The checkbox form field with label.
 */
fun checkbox(
    element: Element,
    context: Context,
): FieldWithLabel<Checkbox> {
    val field = Checkbox(element, context)
    val fieldWithLabel = FieldWithLabel(field, field.label(), context)
    return fieldWithLabel
}
