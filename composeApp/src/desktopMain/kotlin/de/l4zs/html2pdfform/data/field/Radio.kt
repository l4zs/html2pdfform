package de.l4zs.html2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.PdfName
import com.lowagie.text.pdf.RadioCheckField
import de.l4zs.html2pdfform.data.Context
import de.l4zs.html2pdfform.extension.defaultRectangle
import de.l4zs.html2pdfform.util.Actions
import org.jsoup.nodes.Element

class Radio(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
    radioGroup: PdfFormField,
) : FormField(FieldType.RADIO, element, context, id) {
    private val checked = value == radioGroup.get(PdfName.V).toString().substring(1)

    init {
        rectangle = element.defaultRectangle()
        val radio = RadioCheckField(context.writer, rectangle.toPdfRectangle(), null, value ?: "$id")
        radio.checkType = RadioCheckField.TYPE_CIRCLE
        radio.isChecked = checked
        field = radio.fullField
        field.setDefaultValueAsString(if (checked) radio.onValue else "Off")

        field.setAdditionalActions(
            PdfFormField.AA_UP,
            PdfAction.javaScript(Actions.RadioGroup.toggleFields(name ?: ""), context.writer),
        )
    }
}

fun radio(
    element: Element,
    context: Context,
    radioGroup: PdfFormField,
): Radio = Radio(element, context, radioGroup = radioGroup)
