package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.data.Rectangle
import de.janniskramer.htmlform2pdfform.extensions.capitalize
import de.janniskramer.htmlform2pdfform.extensions.height
import de.janniskramer.htmlform2pdfform.extensions.width
import org.jsoup.nodes.Element

abstract class FormField(
    val type: FieldType,
    val element: Element,
    val id: Int,
) {
    val name: String? = element.attr("name").ifBlank { null }
    val value: String? = element.attr("value").ifBlank { null }
    val placeholder: String = element.attr("placeholder")
    val required: Boolean = element.hasAttr("required")
    val readOnly: Boolean = element.hasAttr("readonly")
    val disabled: Boolean = element.hasAttr("disabled")
    val hidden: Boolean = element.hasAttr("hidden")

    val mappingName: String
        get() = "${type.name.capitalize()}-$id"

    fun getRectangle(context: Context): Rectangle =
        context.locationHandler
            .getRectangleFor(
                element.width(),
                element.height(),
            )

    abstract fun write(context: Context): PdfFormField
}

object FormFields
