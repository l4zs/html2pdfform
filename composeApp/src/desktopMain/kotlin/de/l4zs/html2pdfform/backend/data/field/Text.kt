package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfBorderDictionary
import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.RGBColor
import com.lowagie.text.pdf.TextField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.extension.baseFont
import de.l4zs.html2pdfform.backend.extension.defaultRectangle
import de.l4zs.html2pdfform.backend.extension.toPdfRectangle
import de.l4zs.html2pdfform.backend.util.Actions
import de.l4zs.html2pdfform.resources.*
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.action_text_minlength_message
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element
import com.lowagie.text.Element as PdfElement

open class Text(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
    type: FieldType = FieldType.TEXT,
) : FormField(type, element, context, id) {
    private val placeholder: String? = element.attr("placeholder").ifBlank { null }
    private val minLength = element.attr("minlength").toIntOrNull()
    private val maxLength = element.attr("maxlength").toIntOrNull()
    var pattern = element.attr("pattern").ifBlank { null }
    var patternMessage = element.attr("patternMessage").ifBlank { null }

    init {
        field = base().textField
    }

    fun base(): TextField {
        rectangle = element.defaultRectangle(context.config)

        val text =
            TextField(
                context.writer,
                rectangle.toPdfRectangle(),
                mappingName,
            )
        text.mappingName = mappingName
        text.text = value ?: ""
        text.font = context.config.baseFont
        text.fontSize = context.config.fontSize
        text.alignment = PdfElement.ALIGN_LEFT
        text.borderWidth = TextField.BORDER_WIDTH_THIN
        text.borderStyle = PdfBorderDictionary.STYLE_SOLID
        text.borderColor = RGBColor(0, 0, 0)
        text.textColor = RGBColor(0, 0, 0)

        if (maxLength != null) {
            text.maxCharacterLength = maxLength
        }

        return text
    }

    fun addTextActions() {
        if (minLength != null) {
            val message = runBlocking { getString(Res.string.action_text_minlength_message) }
            additionalActions[PdfFormField.AA_JS_CHANGE]!!.add(Actions.Text.validateMinLength(minLength, message))
        }

        if (pattern != null) {
            val defaultMessage = runBlocking { getString(Res.string.action_text_pattern_message) }
            additionalActions[PdfFormField.AA_JS_CHANGE]!!.add(Actions.Text.validatePattern(pattern!!, defaultMessage, patternMessage))
        }

        additionalActions[PdfFormField.AA_JS_FORMAT]!!.add(Actions.Placeholder.formatPlaceholder(placeholder ?: ""))
    }

    override fun write() {
        addTextActions()
        applyWidget()
        setAdditionalActions()
        setDefaults()

        context.acroForm.addFormField(field)
    }
}

fun text(
    element: Element,
    context: Context,
): FieldWithLabel<Text> {
    val text = Text(element, context)
    return FieldWithLabel(text, text.label(), context)
}
