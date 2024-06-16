package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class File(
    element: Element,
    id: Int,
) : Text(element, id, FieldType.FILE) {
    override fun write(context: Context): PdfFormField {
        val field = super.convert(context)

        field.setFieldFlags(PdfFormField.FF_FILESELECT)

        context.acroForm.addFormField(field)

        return field
    }
}

fun FormFields.file(
    element: Element,
    context: Context,
): FieldWithLabel<File> {
    val file = File(element, context.currentElementIndex)
    return FieldWithLabel(file, file.label(context))
}
