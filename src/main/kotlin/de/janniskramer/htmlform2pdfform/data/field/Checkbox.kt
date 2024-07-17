package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfBorderDictionary
import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Actions
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.data.Rectangle
import de.janniskramer.htmlform2pdfform.extensions.height
import de.janniskramer.htmlform2pdfform.extensions.width
import org.jsoup.nodes.Element

class Checkbox(
    element: Element,
    context: Context,
) : FormField(FieldType.CHECKBOX, element, context.currentElementIndex, context) {
    private val checked = this.element.hasAttr("checked")
    override val value: String = if (checked) super.value ?: "Off" else "Off"

    init {
        rectangle =
            Rectangle(
                element.width(),
                element.height(),
            )
        field = PdfFormField.createCheckBox(context.writer)
        field.setFieldName(name)
        field.setValueAsName(value)
        field.setDefaultValueAsString(value)
        field.setAppearanceState(value)

        field.setFlags(PdfFormField.FLAGS_PRINT)
        field.setBorderStyle(PdfBorderDictionary(1.0f, 0))

        if (element.hasAttr("toggles")) {
            field.setAdditionalActions(
                PdfFormField.AA_JS_CHANGE,
                PdfAction.javaScript(Actions.Checkbox.toggleFields(toggles), context.writer),
            )
        }
    }

    override fun applyWidget() {
        super.applyWidget()
        context.acroForm.drawCheckBoxAppearences(
            field,
            value,
            rectangle.llx,
            rectangle.lly,
            rectangle.urx,
            rectangle.ury,
        )
    }
}

fun checkbox(
    element: Element,
    context: Context,
): FieldWithLabel<Checkbox> {
    val field = Checkbox(element, context)
    val label = field.label(context)
    val fieldWithLabel = FieldWithLabel(field, label)
    return fieldWithLabel
}
