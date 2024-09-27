package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.extension.defaultRectangle
import de.l4zs.html2pdfform.backend.util.Actions
import org.jsoup.nodes.Element

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
            name ?: mappingName,
            value,
            checked,
            rectangle.llx,
            rectangle.lly,
            rectangle.urx,
            rectangle.ury,
        )

        if (element.hasAttr("toggles")) {
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

fun checkbox(
    element: Element,
    context: Context,
): FieldWithLabel<Checkbox> {
    val field = Checkbox(element, context)
    val fieldWithLabel = FieldWithLabel(field, field.label(), context)
    return fieldWithLabel
}
