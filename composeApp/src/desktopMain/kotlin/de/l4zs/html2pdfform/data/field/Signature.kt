package de.l4zs.html2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.data.Context
import de.l4zs.html2pdfform.extension.defaultRectangle
import org.jsoup.nodes.Element
import java.awt.Color

class Signature(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : FormField(FieldType.SIGNATURE, element, context, id) {
    init {
        rectangle = element.defaultRectangle()
        field = PdfFormField.createSignature(context.writer)
        field.setFieldName(name)
        field.setFlags(PdfFormField.FLAGS_PRINT)
        field.setMKBorderColor(Color.black)
        field.setMKBackgroundColor(Color.white)
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

fun signature(
    element: Element,
    context: Context,
): FieldWithLabel<Signature> {
    val signature = Signature(element, context)
    return FieldWithLabel(signature, signature.label(), context)
}
