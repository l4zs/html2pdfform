package de.l4zs.html2pdfform.data.field

import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.ColumnText
import de.l4zs.html2pdfform.config.config
import de.l4zs.html2pdfform.config.defaultFont
import de.l4zs.html2pdfform.data.Context
import de.l4zs.html2pdfform.extension.defaultRectangle
import de.l4zs.html2pdfform.extension.findLabel
import org.jsoup.nodes.Element

class Label(
    element: Element,
    context: Context,
    required: Boolean,
    id: Int = context.currentElementIndex,
) : FormField(FieldType.LABEL, element, context, id) {
    var text: Paragraph

    init {
        if (required) {
            element.appendText(" *")
        }
        rectangle = element.defaultRectangle()
        text = Paragraph(element.text(), config.defaultFont)
    }

    override fun write() {
        val columnText = ColumnText(context.writer.directContent)
        columnText.addText(text)
        columnText.setSimpleColumn(rectangle.llx, rectangle.lly, rectangle.urx, rectangle.ury)
        columnText.go()
    }
}

fun FormField.label(): Label? {
    val label = element.findLabel() ?: return null
    return Label(
        label,
        context,
        required,
    )
}

fun fakeLabel(
    context: Context,
    text: String,
    required: Boolean = false,
): Label =
    Label(
        Element("label").text(text),
        context,
        required,
    )
