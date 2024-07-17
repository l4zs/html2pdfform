package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfBorderDictionary
import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.RGBColor
import com.lowagie.text.pdf.TextField
import de.janniskramer.htmlform2pdfform.config
import de.janniskramer.htmlform2pdfform.data.Actions
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element
import com.lowagie.text.Element as PdfElement

open class Text(
    element: Element,
    id: Int,
    type: FieldType = FieldType.TEXT,
) : FormField(type, element, id) {
    private val minLength = element.attr("minlength").toIntOrNull()
    private val maxLength = element.attr("maxlength").toIntOrNull()
    private val pattern = element.attr("pattern").ifBlank { null }

    override fun write(context: Context): PdfFormField =
        convert(context).also {
            context.acroForm.addFormField(it)
        }

    fun convert(context: Context): PdfFormField {
        val text = base(context)

        val field = text.textField

        field.addTextActions(context)

        return field
    }

    fun base(context: Context): TextField {
        val rectangle = getDefaultRectangle(context)

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

    fun PdfFormField.addTextActions(context: Context) {
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
    val text = Text(element, context.currentElementIndex)
    return FieldWithLabel(text, text.label(context))
}
