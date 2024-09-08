package de.l4zs.html2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfBorderDictionary
import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.RGBColor
import com.lowagie.text.pdf.TextField
import de.l4zs.html2pdfform.ui.config
import de.l4zs.html2pdfform.data.Context
import de.l4zs.html2pdfform.extension.defaultRectangle
import de.l4zs.html2pdfform.util.Actions
import org.jsoup.nodes.Element
import com.lowagie.text.Element as PdfElement

open class Text(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
    type: FieldType = FieldType.TEXT,
) : FormField(type, element, context, id) {
    private val minLength = element.attr("minlength").toIntOrNull()
    private val maxLength = element.attr("maxlength").toIntOrNull()
    private val pattern = element.attr("pattern").ifBlank { null }

    init {
        convert()
    }

    fun convert() {
        val text = base()

        field = text.textField

        field.addTextActions()
    }

    fun base(): TextField {
        rectangle = element.defaultRectangle()

        val text =
            TextField(
                context.writer,
                rectangle.toPdfRectangle(),
                name ?: mappingName,
            )
        text.mappingName = mappingName
        text.text = placeholder ?: value ?: ""
        text.font = config.baseFont
        text.fontSize = config.fontSize
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

    fun PdfFormField.addTextActions() {
        if (required) {
            setFieldFlags(PdfFormField.FF_REQUIRED)
        }
        setDefaultValueAsString(placeholder ?: value ?: "")
        if (readOnly || disabled) {
            setFieldFlags(PdfFormField.FF_READ_ONLY)
        }

        /*
            Additional Actions:
            - PdfFormField.AA_JS_KEY: JavaScript keystroke action
            - PdfFormField.AA_JS_CHANGE: JavaScript validation action
            - PdfFormField.AA_JS_OTHER:CHANGE: JavaScript calculation action
            - PdfFormField.AA_JS_FORMAT: JavaScript format action
         */

        if (minLength != null) {
            setAdditionalActions(
                PdfFormField.AA_JS_CHANGE,
                PdfAction.javaScript(
                    Actions.Text.validateMinLength(minLength),
                    context.writer,
                ),
            )
        }

        if (pattern != null) {
            setAdditionalActions(
                PdfFormField.AA_JS_CHANGE,
                PdfAction.javaScript(
                    Actions.Text.validatePattern(pattern),
                    context.writer,
                ),
            )
        }
    }
}

fun text(
    element: Element,
    context: Context,
): FieldWithLabel<Text> {
    val text = Text(element, context)
    return FieldWithLabel(text, text.label(), context)
}
