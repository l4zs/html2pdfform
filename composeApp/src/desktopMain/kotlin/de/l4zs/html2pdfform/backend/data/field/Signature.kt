package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.extension.defaultRectangle
import org.jsoup.nodes.Element
import java.awt.Color

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
        field.setFieldName(name)
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

fun signature(
    element: Element,
    context: Context,
): FieldWithLabel<Signature> {
    val signature = Signature(element, context)
    return FieldWithLabel(signature, signature.label(), context)
}
