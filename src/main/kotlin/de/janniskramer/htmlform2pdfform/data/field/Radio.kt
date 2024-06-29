package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.PdfName
import com.lowagie.text.pdf.RadioCheckField
import de.janniskramer.htmlform2pdfform.data.Actions
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Radio(
    element: Element,
    id: Int,
    radioGroup: PdfFormField,
) : FormField(FieldType.RADIO, element, id) {
    private val checked = value == radioGroup.get(PdfName.V).toString().substring(1)

    override fun write(context: Context): PdfFormField {
        val rectangle = getRectangle(context)

        val radio = RadioCheckField(context.writer, rectangle.toPdfRectangle(), null, value ?: "$id")
        radio.checkType = RadioCheckField.TYPE_CIRCLE
        radio.isChecked = checked
        val field = radio.fullField
        field.setDefaultValueAsString(if (checked) radio.onValue else "Off")
        if (readOnly || disabled) {
            field.setFieldFlags(PdfFormField.FF_READ_ONLY)
        }
        if (required) field.setFieldFlags(PdfFormField.FF_REQUIRED)
        field.setMappingName(mappingName)

        field.setAdditionalActions(
            PdfFormField.AA_UP,
            PdfAction.javaScript(Actions.RadioGroup.toggleFields(name ?: ""), context.writer),
        )

        return field
    }
}

fun FormFields.radio(
    element: Element,
    context: Context,
    radioGroup: PdfFormField,
): Radio = Radio(element, context.currentElementIndex, radioGroup)
