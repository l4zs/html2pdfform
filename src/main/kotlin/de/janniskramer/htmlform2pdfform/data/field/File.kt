package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class File(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.FILE) {
    init {
        field.setFieldFlags(PdfFormField.FF_FILESELECT)
    }
}

fun file(
    element: Element,
    context: Context,
): FieldWithLabel<File> {
    val file = File(element, context)
    return FieldWithLabel(file, file.label(), context)
}
