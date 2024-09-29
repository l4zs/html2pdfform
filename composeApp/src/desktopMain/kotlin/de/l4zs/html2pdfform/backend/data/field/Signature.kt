package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.extension.defaultRectangle
import org.jsoup.nodes.Element
import java.awt.Color

/**
 * Represents a signature field.
 *
 * @param element The HTML element.
 * @param context The context.
 * @param id The field ID.
 */
class Signature(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : FormField(FieldType.SIGNATURE, element, context, id) {
    init {
        rectangle = element.defaultRectangle(context.config)
        field = PdfFormField.createSignature(context.writer)
    }

    override fun write() {
        field.setFieldName(mappingName)
        field.setMKBorderColor(Color.black)
        field.setMKBackgroundColor(Color.white)
        applyWidget()
        setAdditionalActions()
        setDefaults()

        context.acroForm.drawSignatureAppearences(
            field,
            rectangle.llx,
            rectangle.lly,
            rectangle.urx,
            rectangle.ury,
        )
        context.acroForm.addFormField(field)
    }
}

/**
 * Creates a signature form field. If a label is present, it will be used as
 * the field label.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The signature form field with label.
 */
fun signature(
    element: Element,
    context: Context,
): FieldWithLabel<Signature> {
    val signature = Signature(element, context)
    return FieldWithLabel(signature, signature.label(), context)
}
