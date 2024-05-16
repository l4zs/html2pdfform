package de.jannis_kramer.htmlform2pdfform.convert

import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.BaseFont
import com.lowagie.text.pdf.ColumnText
import com.lowagie.text.pdf.PdfAcroForm
import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.PdfName
import com.lowagie.text.pdf.PdfNumber
import com.lowagie.text.pdf.PdfWriter
import org.jsoup.Jsoup
import java.io.File
import com.lowagie.text.Document as PdfDocument
import org.jsoup.nodes.Document as HtmlDocument
import org.jsoup.nodes.Element as HtmlElement


fun main() {
    val input = File("files/form.html")
    val output = File("files/form.pdf")

    HtmlConverter().parse(input, output)
}

class HtmlConverter {

    data class Rect(
        val llx: Float,
        val lly: Float,
        val urx: Float,
        val ury: Float,
    ) {
        val width: Float
            get() = urx - llx
        val height: Float
            get() = ury - lly
    }

    val pdf by lazy { PdfDocument() }

    private val baseFont = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
    private val defaultFontSize = 16f
    private val defaultFont = Font(baseFont, defaultFontSize)
    private val defaultSmallFont = Font(baseFont, defaultFontSize * 3 / 4)
    private val defaultFontWidth = baseFont.getWidthPoint("a", defaultFontSize)

    private val pageWidth = 595f
    private val pageHeight = 842f

    private val pagePaddingX = 50f
    private val pagePaddingY = 50f

    private val objectPadding = 20f
    private val innerObjectPadding = 5f

    private val effectivePageWidth = pageWidth - pagePaddingX * 2
    private val effectivePageHeight = pageHeight - pagePaddingY * 2

    private val pageMinX = pagePaddingX
    private val pageMaxX = pageWidth - pagePaddingX
    private val pageMinY = pagePaddingY
    private val pageMaxY = pageHeight - pagePaddingY

    private var currentX = pageMinX
    private var currentY = pageMaxY

    private val _radioGroups = mutableMapOf<PdfAcroForm, MutableMap<String, PdfFormField>>()
    private val PdfAcroForm.radioGroups: MutableMap<String, PdfFormField>
        get() = _radioGroups.getOrPut(this) { mutableMapOf() }

    private var currentElementIndex = 0
        get() {
            field += 1
            return field
        }

    private fun getRectangleFor(width: Float, height: Float): Rect {
        if (currentY - height < pageMinY) {
            pdf.newPage()
            currentY = pageMaxY
            currentX = pageMinX
        }
        if (currentX + width > pageMaxX) {
            currentX = pageMinX
            currentY -= height + objectPadding
        }
        val llx = currentX
        val lly = currentY - height
        val urx = llx + width
        val ury = lly + height
        currentX = pageMinX
        currentY -= height + objectPadding
//        currentX += width + objectPadding
        return Rect(llx, lly, urx, ury)
    }

    fun parse(input: File, output: File) {
        val html = Jsoup.parse(input)

        pdf.use {
            val writer = PdfWriter.getInstance(pdf, output.outputStream())
            writer.setPdfVersion(PdfWriter.PDF_VERSION_1_7)
            pdf.open()
            convertForms(pdf, html, writer)
        }
    }

    private fun convertForms(document: PdfDocument, html: HtmlDocument, writer: PdfWriter) {
        html.forms().forEach { htmlForm ->
            val acroForm = writer.acroForm

//            htmlForm.children().forEach { element ->
//                convertFormField(element, document, acroForm, writer)
//            }

            htmlForm.children().toList().filter {
                it.tagName().lowercase() == "input" || it.tagName() == "fieldset"
            }.associateWith { input ->
                htmlForm.children().firstOrNull { it.tagName().lowercase() == "label" && it.attr("for") == input.attr("id") }
            }.forEach inputs@{ (input, maybeLabel) ->
                if (input.tagName() == "fieldset") {
                    val radios = input.children().toList().filter { it.tagName() == "input" && it.attr("type") == "radio" }
                        .associateWith { radio ->
                            input.children().firstOrNull {
                                it.tagName() == "label" && it.attr("for") == radio.attr("id")
                            } ?: HtmlElement("label").apply {
                                attr("for", radio.id())
                                text(radio.attr("value").capitalizeFirst())
                            }
                        }

                    val title = input.children().firstOrNull { it.tagName() == "legend" } ?: HtmlElement("legend").apply {
                        text(radios.keys.first().attr("name").capitalizeFirst())
                    }
                    val titleWidth = baseFont.getWidthPoint(title.text(), defaultFontSize) + 0.01f
                    val titleHeight = defaultFontSize
                    val maxLabelWidth = radios.values.map { it.text() }.maxOf { baseFont.getWidthPoint(it, defaultFontSize) } + 0.01f
                    val labelHeight = defaultFontSize
                    val radioWidth = defaultFontSize
                    val radioHeight = defaultFontSize
                    val width = maxOf(titleWidth, maxLabelWidth + innerObjectPadding + radioWidth)
                    val height = titleHeight + innerObjectPadding + radios.size * (maxOf(radioHeight, labelHeight) + innerObjectPadding)

                    val group = acroForm.getRadioGroup(
                        radios.keys.first().attr("name"),
                        radios.keys.first().attr("value"),
                        false,
                    )

                    val rect = getRectangleFor(width, height)
                    val titleRect = rect.copy(lly = rect.ury - defaultFontSize, urx = rect.llx + titleWidth)
                    convertLabelField(title, document, writer, titleRect)
                    var tmpY = rect.ury - titleHeight - innerObjectPadding
                    println("Radio Group: $group")
                    println("rect: $rect")
                    radios.forEach { (radio, label) ->
                        println("Label: $label")

                        val maxHeight = maxOf(radioHeight, labelHeight)
                        val lly = tmpY - maxHeight
                        val labelWidth = baseFont.getWidthPoint(label.text(), defaultFontSize) + 0.01f
                        var tmpX = rect.llx
                        val radioRect = rect.copy(lly = lly, ury = lly + radioHeight, llx = tmpX, urx = tmpX + radioWidth)
                        tmpX += radioWidth + innerObjectPadding
                        val labelRect = rect.copy(lly = lly, ury = lly + labelHeight, llx = tmpX, urx = tmpX + labelWidth)
                        tmpY -= maxHeight + innerObjectPadding

                        println("labelRect: $labelRect")
                        println("radioRect: $radioRect")
                        println()

                        convertRadioField(radio, acroForm, writer, radioRect, group)
                        convertLabelField(label, document, writer, labelRect)
                    }
                    writer.addAnnotation(group)
                    return@inputs
                }

                val label = maybeLabel ?: HtmlElement("label").apply {
                    attr("for", input.id())
                    val text = if (input.attr("type") == "radio") {
                        input.attr("value")
                    } else {
                        input.attr("name").ifBlank { input.attr("id") }.ifBlank { input.attr("type") }
                    }.capitalizeFirst()
                    text(text)
                }

                val (inputWidth, inputHeight) = if (input.hasAttr("size")) {
                    val size = input.attr("size").toInt()
                    size * defaultFontWidth to defaultFontSize + innerObjectPadding
                } else {
                    200f to defaultFontSize + innerObjectPadding
                }
                val (labelWidth, labelHeight) = (baseFont.getWidthPoint(
                    label.text(),
                    defaultFontSize
                ) + 0.01f) to defaultFontSize // Add a small padding so the text does not break into two lines

                val width = maxOf(inputWidth, labelWidth)
                val height = labelHeight + innerObjectPadding + inputHeight

                println(input.id())
                println("labelHeight: $labelHeight")
                println("inputHeight: $inputHeight")

                val rect = getRectangleFor(width, height)
                val labelRect = rect.copy(lly = rect.ury - labelHeight, urx = rect.llx + labelWidth)
                val inputRect = rect.copy(ury = rect.lly + inputHeight, urx = rect.llx + inputWidth)

                println("rect: $rect")
                println("labelRect: $labelRect")
                println("inputRect: $inputRect")
                println()

                convertLabelField(label, document, writer, labelRect)
                convertInputField(input, acroForm, writer, inputRect)
            }

            acroForm.radioGroups.forEach { (_, radio) ->
                writer.addAnnotation(radio)
            }
        }
    }

    private fun convertFormField(element: HtmlElement, document: PdfDocument, form: PdfAcroForm, writer: PdfWriter) {
        when (val tagName = element.tagName().lowercase()) {
            "input" -> convertInputField(element, form, writer)
            "textarea" -> convertTextField(element, form, writer, true)
//            "label" -> convertLabelField(element, document, writer)
            else -> {
//                throw IllegalArgumentException("Unsupported form field type: $tagName")
            }
        }
    }

    private fun convertLabelField(element: HtmlElement, document: PdfDocument, writer: PdfWriter, rect: Rect) {
        val mappingName = "${element.tagName()}-$currentElementIndex"
        val name = element.attr("for").ifBlank { element.attr("id") }.ifBlank { mappingName }

        val label = Paragraph(element.text(), defaultFont)
        val columnText = ColumnText(writer.directContent)
        columnText.addText(label)
        columnText.setSimpleColumn(rect.llx, rect.lly, rect.urx, rect.ury)
        columnText.go()
    }

    private fun convertInputField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null): PdfFormField? {
        when (val type = element.attr("type")) {
            "button" -> {} // For now, not supported
            "checkbox" -> convertCheckboxField(element, form, writer, rect) // allow fieldset for multiple checkboxes
            "color" -> {} // PDF does not support color input fields
            "date" -> convertDateField(element, form, writer, rect) // add min, max, step
            "datetime-local" -> convertDateTimeLocalField(element, form, writer, rect) // add min, max, step
            "email" -> convertEmailField(element, form, writer, rect) // add allowance for multiple email addresses if specified
            "file" -> convertFileField(element, form, writer, rect) // should be done
            "hidden" -> {} // For now, not supported
            "image" -> {} // For now, not supported
            "month" -> {} // For now, not supported, use date field with custom format instead
            "number" -> convertNumberField(element, form, writer, rect) // done
            "password" -> convertPasswordField(element, form, writer, rect) // maybe add password stars when not editing?
            "radio" -> convertRadioField(element, form, writer, rect) // much to do
            "range" -> {} // TODO("Use text field with range slider")
            "reset" -> convertResetField(element, form, writer, rect) // done
            "search" -> {} // TODO("Use text field with search functionality")
            "submit" -> convertSubmitField(element, form, writer, rect) // add URL option
            "tel" -> {} // TODO("Use text field with telephone number formatting")
            "text" -> convertTextField(element, form, writer, false, rect) // fix required and readonly, generally for all types
            "time" -> {} // TODO("Use text field with time formatting")
            "url" -> {} // TODO("Use text field with URL validation")
            "week" -> {} // TODO("Use text field with week formatting")
            else -> {
//                throw IllegalArgumentException("Unsupported input field type: $type")
            }
        }
        return null
    }

    private fun convertCheckboxField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null): PdfFormField {
        val (llx, lly, urx, ury) = rect?.copy(urx = rect.llx + rect.height) ?: getRectangleFor(defaultFontSize, defaultFontSize)

        val mappingName = "${element.tagName()}-$currentElementIndex"
        val name = element.attr("name").ifBlank { element.attr("id") }.ifBlank { mappingName }

        return form.addCheckBox(
            name,
            element.attr("value"),
            element.hasAttr("checked"),
            llx,
            lly,
            urx,
            ury,
        ).apply {
            setMappingName(mappingName)
        }
    }

    private fun convertDateField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null): PdfFormField {
        return convertDateTimeField(element, form, writer, "dd.mm.yyyy", rect)
    }

    private fun convertDateTimeLocalField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null): PdfFormField {
        return convertDateTimeField(element, form, writer, "dd.mm.yyyy HH:MM", rect)
    }

    private fun convertMonthField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null): PdfFormField {
        return convertDateTimeField(element, form, writer, "mmm yyyy", rect)
    }

    private fun convertTimeField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null): PdfFormField {
        return convertDateTimeField(element, form, writer, "MM:HH tt", rect)
    }

    private fun convertWeekField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null): PdfFormField {
        val field = convertTextField(element, form, writer, false, rect)

        val weekValidation = """
            var weekRegex = /^\\d{4}-W\\d{2}$/;
            if (event.value && !global.isResettingForm) {
                event.rc = 
                if (!event.rc) {
                    app.alert("Invalid week format.");
                }
            }
        """.trimIndent()
        field.setAdditionalActions(PdfName.V, PdfAction.javaScript(weekValidation, writer))

        if (element.hasAttr("min")) {
            val minValidation = """
                if (event.value && !global.isResettingForm) {
                    event.rc = event.value >= "${element.attr("min")}";
                    if (!event.rc) {
                        app.alert("Please enter a week greater or equal to ${element.attr("min")}");
                    }
                }
            """.trimIndent()
        }

        return field
    }

    private fun convertDateTimeField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        defaultFormat: String,
        rect: Rect? = null,
    ): PdfFormField {
        val field = convertTextField(element, form, writer, false, rect)

        val format = element.attr("format").ifBlank { defaultFormat }

        field.setDefaultValueAsName(element.attr("value"))
        field.setAdditionalActions(PdfName.F, PdfAction.javaScript("AFDate_FormatEx(\"$format\");", writer))
        field.setAdditionalActions(PdfName.K, PdfAction.javaScript("AFDate_KeystrokeEx(\"$format\");", writer))

        if (element.hasAttr("min")) {
            // TODO: Test this, probably does not work
            val minValidation = """
                if (event.value && !global.isResettingForm) {
                    event.rc = event.value >= "${element.attr("min")}";
                    if (!event.rc) {
                        app.alert("Please enter a date greater or equal to ${element.attr("min")}");
                    }
                }
            """.trimIndent()

            field.setAdditionalActions(PdfName.V, PdfAction.javaScript(minValidation, writer))
        }

        if (element.hasAttr("max")) {
            // TODO: Test this, probably does not work
            val maxValidation = """
                if (event.value && !global.isResettingForm) {
                    event.rc = event.value <= "${element.attr("max")}";
                    if (!event.rc) {
                        app.alert("Please enter a date less or equal to ${element.attr("max")}");
                    }
                }
            """.trimIndent()

            field.setAdditionalActions(PdfName.V, PdfAction.javaScript(maxValidation, writer))
        }

        if (element.hasAttr("step")) {
            // TODO: Test this, probably does not work
            val stepValidation = """
                if (event.value && !global.isResettingForm) {
                    event.rc = event.value % ${element.attr("step")} === 0;
                    if (!event.rc) {
                        app.alert("Please enter a date that is a multiple of ${element.attr("step")}");
                    }
                }
            """.trimIndent()

            field.setAdditionalActions(PdfName.V, PdfAction.javaScript(stepValidation, writer))
        }

        return field
    }

    private fun convertEmailField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null): PdfFormField {
        val field = convertTextField(element, form, writer, false, rect)

        val emailValidation = """
            if (event.value && !global.isResettingForm) {
                event.rc = CBIsValidEmail(event.value);
                if (!event.rc) {
                    app.alert("Invalid email address.");
                }
            }
        """.trimIndent()
        field.setAdditionalActions(PdfName.V, PdfAction.javaScript(emailValidation, writer))

        return field
    }

    private fun convertFileField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null): PdfFormField {
        val field = convertTextField(element, form, writer, false, rect)

        field.addFlags(PdfFormField.FF_FILESELECT)

        return field
    }

    private fun convertNumberField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null): PdfFormField {
        val field = convertTextField(element, form, writer, false, rect)

        val numberValidation = """
            var numberRegex = /^\d*$/;
            if (!event.willCommit && event.change && !global.isResettingForm) {
                event.rc = numberRegex.test(event.change);
            }
        """.trimIndent()
        field.setAdditionalActions(PdfName.K, PdfAction.javaScript(numberValidation, writer))

        // TODO: Handle list of suggested values

        if (element.hasAttr("min")) {
            val minValidation = """
                if (event.value && !global.isResettingForm) {
                    event.rc = event.value >= ${element.attr("min").toInt()};
                    if (!event.rc) {
                        app.alert("Please enter a number greater or equal to ${element.attr("min").toInt()}");
                    }
                }
            """.trimIndent()

            field.setAdditionalActions(PdfName.V, PdfAction.javaScript(minValidation, writer))
        }

        if (element.hasAttr("max")) {
            val maxValidation = """
                if (event.value && !global.isResettingForm) {
                    event.rc = event.value <= ${element.attr("max").toInt()};
                    if (!event.rc) {
                        app.alert("Please enter a number less or equal to ${element.attr("max").toInt()}");
                    }
                }
            """.trimIndent()

            field.setAdditionalActions(PdfName.V, PdfAction.javaScript(maxValidation, writer))
        }

        if (element.hasAttr("step")) {
            val base = if (element.hasAttr("min")) {
                element.attr("min").toInt()
            } else if (element.hasAttr("value")) {
                element.attr("value").toInt()
            } else {
                element.attr("step").toInt()
            }
            val step = element.attr("step").toInt()

            val stepValidation = """
                if (event.value && !global.isResettingForm) {
                    event.rc = event.value % $step === $base;
                    if (!event.rc) {
                        app.alert("Please enter a number that is a multiple of $step with a base of $base.");
                    }
                }
            """.trimIndent()

            field.setAdditionalActions(PdfName.V, PdfAction.javaScript(stepValidation, writer))
        }

        return field
    }

    private fun convertPasswordField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null): PdfFormField {
        val field = convertTextField(element, form, writer, false, rect)

        field.addFlags(PdfFormField.FF_PASSWORD)

        return field
    }

    private fun convertRadioField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null, group: PdfFormField? = null) {
        val (llx, lly, urx, ury) = rect?.copy(urx = rect.llx + rect.height) ?: getRectangleFor(defaultFontSize, defaultFontSize)

        val realGroup = group ?: form.radioGroups.getOrPut(element.attr("name")) {
            form.getRadioGroup(
                element.attr("name"),
                element.attr("value"),
                true,
            )
        }

        if (element.hasAttr("checked")) {
            realGroup.setValueAsName(element.attr("value"))
        }

        val mappingName = "${element.tagName()}-$currentElementIndex"

        form.addRadioButton(
            realGroup,
            element.attr("value"),
            llx,
            lly,
            urx,
            ury,
        ).apply {
            setMappingName(mappingName)
        }
    }

    private fun convertResetField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null): PdfFormField {
        val (llx, lly, urx, ury) = rect ?: getRectangleFor(50f, defaultFontSize + innerObjectPadding)

        val mappingName = "${element.tagName()}-$currentElementIndex"
        val name = element.attr("name").ifBlank { element.attr("id") }.ifBlank { mappingName }
        val value = element.attr("value").ifBlank { "Reset" }

        val resetPushAction = """
            global.isResettingForm = true;
        """.trimIndent()

        val resetReleaseAction = """
            global.isResettingForm = false;
        """.trimIndent()

        return form.addResetButton(
            name,
            value,
            value,
            baseFont,
            defaultFontSize,
            llx,
            lly,
            urx,
            ury,
        ).apply {
            setMappingName(mappingName)
            setAdditionalActions(PdfName.D, PdfAction.javaScript(resetPushAction, writer))
            setAdditionalActions(PdfName.BL, PdfAction.javaScript(resetReleaseAction, writer))
        }
    }

    private fun convertSubmitField(element: HtmlElement, form: PdfAcroForm, writer: PdfWriter, rect: Rect? = null): PdfFormField {
        val (llx, lly, urx, ury) = rect ?: getRectangleFor(50f, defaultFontSize + innerObjectPadding)

        val mappingName = "${element.tagName()}-$currentElementIndex"
        val name = element.attr("name").ifBlank { element.attr("id") }.ifBlank { mappingName }
        val value = element.attr("value").ifBlank { "Submit" }

        return form.addHtmlPostButton(
            name,
            value,
            value,
            "", // TODO: Add URL
            baseFont,
            defaultFontSize,
            llx,
            lly,
            urx,
            ury,
        ).apply {
            setMappingName(mappingName)
        }
    }

    private fun convertTextField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        multiline: Boolean = false,
        rect: Rect? = null,
    ): PdfFormField {
        val width = if (element.hasAttr("size")) {
            val size = element.attr("size").toInt()
            innerObjectPadding + size * defaultFontWidth + innerObjectPadding
        } else {
            200f
        }
        val height = defaultFontSize + innerObjectPadding

        val (llx, lly, urx, ury) = rect ?: getRectangleFor(width, height)

        /*
            Actions:
            - K: JavaScript keystroke action
            - V: JavaScript validation action
            - C: JavaScript calculation action
            - F: JavaScript format action
         */

        val mappingName = "${element.tagName().lowercase()}-$currentElementIndex"
        val name = element.attr("name").ifBlank { element.attr("id") }.ifBlank { mappingName }

        val field = if (multiline) {
            form.addMultiLineTextField(
                name,
                element.attr("placeholder"),
                baseFont,
                defaultFontSize,
                llx,
                lly,
                urx,
                ury,
            )
        } else {
            form.addSingleLineTextField(
                name,
                element.attr("placeholder"),
                baseFont,
                defaultFontSize,
                llx,
                lly,
                urx,
                ury,
            )
        }.apply {
            setMappingName(mappingName)
        }

        if (element.hasAttr("required")) {
            // TODO: Does not work right now, if set the field does simply not show up
//            field.addFlags(PdfFormField.FF_REQUIRED)
        }

        if (element.hasAttr("disabled") || element.hasAttr("readonly")) {
            // TODO: Does not work right now, if set the field is still editable
//            field.addFlags(PdfFormField.FF_READ_ONLY)
        }

        if (element.hasAttr("maxlength")) {
            field.put(PdfName.MAXLEN, PdfNumber(element.attr("maxlength").toInt()))
        }

        if (element.hasAttr("minlength")) {
            val validateMinLength = """
                if (event.value && !global.isResettingForm) {
                    event.rc = event.value.length >= ${element.attr("minlength").toInt()}
                    if (!event.rc) {
                        app.alert("Please enter at least ${element.attr("minlength").toInt()} characters.");
                    }
                }
            """.trimIndent()

            field.setAdditionalActions(PdfName.V, PdfAction.javaScript(validateMinLength, writer))
        }

        if (element.hasAttr("pattern")) {
            val validatePattern = """
            var regex = RegExp("${element.attr("pattern")}");
            if (event.value && !global.isResettingForm) {
                event.rc = regex.test(event.value);
                if (!event.rc) {
                    app.alert("Invalid input. Please follow the pattern: ${element.attr("pattern")}");
                }
            }
            """.trimIndent()

            field.setAdditionalActions(PdfName.V, PdfAction.javaScript(validatePattern, writer))
        }

        /*
            field.addFlags(PdfFormField.FF_COMB) // space value evenly across the width of the field (for single-line text fields)
            field.addFlags(PdfFormField.FF_REQUIRED) // field must be filled in
            field.addFlags(PdfFormField.FF_PASSWORD) // field is a password field
            field.addFlags(PdfFormField.FF_READ_ONLY) // field is read-only
            field.put(PdfName.MAXLEN, PdfNumber(10)) // maximum length of text field (in characters, here: 10)

            field.addFlags(PdfFormField.FF_DONOTSPELLCHECK) // field must not be spell-checked
         */

        return field
    }
}
