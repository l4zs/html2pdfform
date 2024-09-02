package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.ColumnText
import de.janniskramer.htmlform2pdfform.config
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.extension.defaultRectangle
import de.janniskramer.htmlform2pdfform.extension.findLabel
import org.jsoup.nodes.Element

class Label(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : FormField(FieldType.LABEL, element, context, id) {
    var text: Paragraph

    init {
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
    )
}

fun fakeLabel(
    context: Context,
    text: String,
): Label =
    Label(
        Element("label").text(text),
        context,
    )
