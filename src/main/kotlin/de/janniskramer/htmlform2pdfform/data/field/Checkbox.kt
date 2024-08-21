package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Actions
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.extensions.defaultRectangle
import org.jsoup.nodes.Element

class Checkbox(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : FormField(FieldType.CHECKBOX, element, context, id) {
    private val checked = this.element.hasAttr("checked")
    override val value: String = if (checked) super.value ?: "Off" else "Off"

    init {
        rectangle = element.defaultRectangle()
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
        field.setDefaultValueAsString(value)

        if (element.hasAttr("toggles")) {
            field.setAdditionalActions(
                PdfFormField.AA_JS_CHANGE,
                PdfAction.javaScript(Actions.Checkbox.toggleFields(toggles), context.writer),
            )
        }
    }

    override fun write() {
        super.applyWidget()
        field.setPage()

        if (readOnly || disabled) {
            field.setFieldFlags(PdfFormField.FF_READ_ONLY)
        }
        if (required) {
            field.setFieldFlags(PdfFormField.FF_REQUIRED)
        }
        field.setMappingName(mappingName)

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
