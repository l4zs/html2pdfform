package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.PdfName
import com.lowagie.text.pdf.RadioCheckField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.extension.defaultRectangle
import de.l4zs.html2pdfform.backend.extension.toPdfRectangle
import org.jsoup.nodes.Element

/**
 * Represents a radio button field.
 *
 * @param element The HTML element
 * @param context The context
 * @param id The id of the radio button
 * @param radioGroup The radio group the radio button belongs to
 * @constructor Creates a radio button field
 */
class Radio(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
    radioGroup: PdfFormField,
) : FormField(FieldType.RADIO, element, context, id) {
    private val checked = value == radioGroup.get(PdfName.V).toString().substring(1)

    init {
        rectangle = element.defaultRectangle(context.config)
        val radio = RadioCheckField(context.writer, rectangle.toPdfRectangle(), null, value ?: "$id")
        radio.checkType = RadioCheckField.TYPE_CIRCLE
        radio.isChecked = checked
        field = radio.fullField
    }

    override fun write() {
        applyWidget()
        setAdditionalActions()
        setDefaults()
        field.setDefaultValueAsString(if (checked) value ?: "$id" else "Off")

        context.acroForm.addFormField(field)
    }
}

/**
 * Creates a radio form field.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The radio form field.
 */
fun radio(
    element: Element,
    context: Context,
    radioGroup: PdfFormField,
): Radio = Radio(element, context, radioGroup = radioGroup)
