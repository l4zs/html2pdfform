package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.ColumnText
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.extension.defaultFont
import de.l4zs.html2pdfform.backend.extension.defaultRectangle
import de.l4zs.html2pdfform.backend.extension.findLabel
import org.jsoup.nodes.Element

/**
 * Represents a label field.
 *
 * @param element The HTML element.
 * @param context The context.
 * @param required Whether the field is required.
 * @param id The field ID.
 * @property value The label text.
 */
class Label(
    element: Element,
    context: Context,
    required: Boolean,
    id: Int = context.currentElementIndex,
) : FormField(FieldType.LABEL, element, context, id) {
    override val value: String = element.text()
    var text: Paragraph

    init {
        if (required) {
            element.appendText(" *")
        }
        rectangle = element.defaultRectangle(context.config)
        text = Paragraph(value, context.config.defaultFont)
    }

    override fun write() {
        val columnText = ColumnText(context.writer.directContent)
        columnText.addText(text)
        columnText.setSimpleColumn(rectangle.llx, rectangle.lly, rectangle.urx, rectangle.ury)
        columnText.go()
    }
}

/**
 * Searches for a label element for the element.
 *
 * @return The label element or null.
 * @receiver A form field.
 */
fun FormField.label(): Label? {
    val label = element.findLabel() ?: return null
    return Label(
        label,
        context,
        required,
    )
}

/**
 * Creates a fake label.
 *
 * @param context The context.
 * @param text The label text.
 * @param required Whether the field is required.
 * @return The fake label.
 */
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
