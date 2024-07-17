package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Signature(
    element: Element,
    id: Int,
) : FormField(FieldType.SIGNATURE, element, id) {
    override fun write(context: Context): PdfFormField {
        val rectangle = getDefaultRectangle(context)

        val field =
            context.acroForm.addSignature(
                name ?: mappingName,
                rectangle.llx,
                rectangle.lly,
                rectangle.urx,
                rectangle.ury,
            )

        if (required) {
            field.setFieldFlags(PdfFormField.FF_REQUIRED)
        }

        return field
    }
}

fun signature(
    element: Element,
    context: Context,
): FieldWithLabel<Signature> {
    val signature = Signature(element, context.currentElementIndex)
    return FieldWithLabel(signature, signature.label(context))
}
