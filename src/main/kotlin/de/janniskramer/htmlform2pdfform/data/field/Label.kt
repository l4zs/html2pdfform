package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.ColumnText
import de.janniskramer.htmlform2pdfform.Config
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.data.Rectangle
import de.janniskramer.htmlform2pdfform.extensions.findLabel
import de.janniskramer.htmlform2pdfform.extensions.height
import de.janniskramer.htmlform2pdfform.extensions.width
import org.jsoup.nodes.Element

class Label(
    val element: Element,
    val id: Int,
) {
    private fun getRectangle(context: Context): Rectangle =
        context.locationHandler
            .getRectangleFor(
                element.width(),
                element.height(),
            )

    fun write(context: Context) {
        val rectangle = getRectangle(context)
        val label = Paragraph(element.text(), Config.defaultFont)
        val columnText = ColumnText(context.writer.directContent)
        columnText.addText(label)
        columnText.setSimpleColumn(rectangle.llx, rectangle.lly, rectangle.urx, rectangle.ury)
        columnText.go()
    }
}

fun FormField.label(context: Context): Label? {
    val label = element.findLabel() ?: return null
    return Label(
        label,
        context.currentElementIndex,
    )
}

fun FormFields.fakeLabel(
    context: Context,
    text: String,
): Label =
    Label(
        Element("label").text(text),
        context.currentElementIndex,
    )
