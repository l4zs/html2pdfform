package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Checkbox(
    element: Element,
    id: Int,
) : FormField(FieldType.CHECKBOX, element, id) {
    private val checked = this.element.hasAttr("checked")

    override fun write(context: Context): PdfFormField {
        val rectangle = getRectangle(context)

        val field =
            context.acroForm
                .addCheckBox(
                    name ?: mappingName,
                    value ?: "Yes",
                    checked,
                    rectangle.llx,
                    rectangle.lly,
                    rectangle.urx,
                    rectangle.ury,
                )
        if (readOnly || disabled) field.setFieldFlags(PdfFormField.FF_READ_ONLY)
        if (required) field.setFieldFlags(PdfFormField.FF_REQUIRED)
        field.setMappingName(mappingName)

        return field
    }
}

fun FormFields.checkbox(
    element: Element,
    context: Context,
): FieldWithLabel<Checkbox> {
    val checkbox = Checkbox(element, context.currentElementIndex)
    return FieldWithLabel(checkbox, checkbox.label(context))
}
