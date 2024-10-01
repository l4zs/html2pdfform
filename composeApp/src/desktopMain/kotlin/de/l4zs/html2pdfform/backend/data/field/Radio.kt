package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.*
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
    val radioGroup: PdfFormField,
    tpOn: PdfAppearance,
    tpOff: PdfAppearance,
    id: Int = context.currentElementIndex,
) : FormField(FieldType.RADIO, element, context, id) {
    private val checked = value == radioGroup.get(PdfName.V).toString().substring(1)

    init {
        rectangle = element.defaultRectangle(context.config)
        field = PdfFormField.createEmpty(context.writer)
        field.setAppearanceState(if (checked) value ?: "$id" else "Off")
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, value ?: "$id", tpOn)
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", tpOff)
        field.addFlags(PdfFormField.FLAGS_PRINT)
    }

    override fun write() {
        applyWidget()

        radioGroup.addKid(field)
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
    tpOn: PdfAppearance,
    tpOff: PdfAppearance,
): Radio = Radio(element, context, radioGroup, tpOn, tpOff)
