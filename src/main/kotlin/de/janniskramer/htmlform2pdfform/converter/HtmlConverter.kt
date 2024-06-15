package de.janniskramer.htmlform2pdfform.converter

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
import com.lowagie.text.pdf.RadioCheckField
import de.janniskramer.htmlform2pdfform.capitalizeFirst
import de.janniskramer.htmlform2pdfform.findLabelFor
import de.janniskramer.htmlform2pdfform.findRadioGroupFor
import de.janniskramer.htmlform2pdfform.height
import de.janniskramer.htmlform2pdfform.width
import org.jsoup.Jsoup
import java.io.File
import com.lowagie.text.Document as PdfDocument
import org.jsoup.nodes.Document as HtmlDocument
import org.jsoup.nodes.Element as HtmlElement

class HtmlConverter(
    private val config: Config = Config(),
) {
    private val pdf by lazy { PdfDocument() }

    private val baseFont = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
    private val defaultFont = Font(baseFont, config.fontSize)
    private val defaultFontWidth = baseFont.getWidthPoint("a", config.fontSize)

    private var currentX = config.pageMinX
    private var currentY = config.pageMaxY

    private val allConvertedNames = mutableMapOf<PdfAcroForm, MutableList<String>>()
    private val PdfAcroForm.convertedNames: MutableList<String>
        get() = allConvertedNames.getOrPut(this) { mutableListOf() }

    private var currentElementIndex = 0
        get() = field++ // auto-increment

    private var lastLineHeight = 0f

    private fun getRectangleFor(
        width: Float,
        height: Float,
        padXAfter: Float = 0f,
        padYAfter: Float = config.objectPadding,
        forceNewLine: Boolean = true,
        newPageCallback: () -> Boolean = { false },
    ): Rectangle {
        if (currentY - height < config.pageMinY) {
            val shouldReturn = newPageCallback()
            pdf.newPage()
            currentY = config.pageMaxY
            currentX = config.pageMinX
            if (shouldReturn) {
                return Rectangle(
                    currentX,
                    currentY,
                    currentX + width,
                    currentY - height,
                )
            }
        }
        if (currentX + width > config.pageMaxX) {
            forceNewLine()
        }
        lastLineHeight = lastLineHeight.coerceAtLeast(height)
        val llx = currentX
        val lly = currentY - height
        val urx = llx + width
        val ury = lly + height
        if (forceNewLine) {
            forceNewLine(padYAfter)
        } else {
            currentX = urx + padXAfter
            currentY -= padYAfter
        }
        return Rectangle(llx, lly, urx, ury)
    }

    private fun forceNewLine(padYAfter: Float = 0f) {
        currentX = config.pageMinX
        currentY -= lastLineHeight + padYAfter
        lastLineHeight = 0f
    }

    fun parse(
        input: File,
        output: File,
    ) {
        val html =
            try {
                Jsoup.parse(input)
            } catch (exception: java.io.IOException) {
                return // TODO: error handling
            }

        pdf.use {
            val writer = PdfWriter.getInstance(pdf, output.outputStream())
            writer.setPdfVersion(PdfWriter.PDF_VERSION_1_7)
            pdf.open()
            convertForms(pdf, html, writer)
        }
    }

    private fun convertForms(
        document: PdfDocument,
        html: HtmlDocument,
        writer: PdfWriter,
    ) {
        html.forms().forEach { htmlForm ->
            val acroForm = writer.acroForm
            convertFormPart(htmlForm, htmlForm, document, acroForm, writer)
        }
    }

    private fun convertFormPart(
        htmlForm: HtmlElement,
        element: HtmlElement,
        document: PdfDocument,
        acroForm: PdfAcroForm,
        writer: PdfWriter,
    ) {
        /*
         already converted i.e., label for an input or radio group, therefore skip
         this means that labels that are not directly associated with an input field are ignored
         */
        if (acroForm.convertedNames.contains(element.id())) {
            return
        }
        when (element.tagName().lowercase()) {
            "legend" -> {
                convertLabelField(
                    element,
                    document,
                    writer,
                    getRectangleFor(
                        element.width(
                            config.fontSize,
                            baseFont,
                        ) ?: config.inputWidth,
                        config.fontSize,
                        0f,
                        config.innerObjectPadding,
                    ),
                )
            }

            "input", "textarea" -> {
                if (element.attr("type") == "radio") {
                    htmlForm
                        .findRadioGroupFor(element.attr("name"))
                        .map { radio ->
                            radio to htmlForm.findLabelFor(radio.attr("id"))
                        }.let { radios ->
                            convertFormGroup(document, acroForm, writer, *radios.toTypedArray())
                        }
                } else {
                    convertFormGroup(document, acroForm, writer, element to htmlForm.findLabelFor(element.attr("id")))
                }
            }

            else -> {
                element.children().forEach { child ->
                    convertFormPart(htmlForm, child, document, acroForm, writer)
                }
            }
        }
    }

    /**
     * Rectangles are calculated differently based on the input type: <br>
     *
     * `radio` and `checkbox`:
     *
     *   ```
     *      ---------           ---------
     *      | Label | <padding> | Input |
     *      ---------           ---------
     *   ```
     *
     * otherwise:
     *
     *   ```
     *      ---------
     *      | Label |
     *      ---------
     *      <padding>
     *      ---------
     *      | Input |
     *      ---------
     *   ```
     */
    private fun convertFormGroup(
        document: PdfDocument,
        acroForm: PdfAcroForm,
        writer: PdfWriter,
        vararg elements: Pair<HtmlElement, HtmlElement?>,
    ) {
        val radioName = elements.firstOrNull { it.first.attr("type") == "radio" }?.first?.attr("name")
        val checkedElement =
            elements.firstOrNull { it.first.hasAttr("checked") }?.first?.attr("value")
                ?: elements.first().first.attr("value")
        val radioGroup =
            if (radioName != null) {
                acroForm.getRadioGroup(radioName, checkedElement, false)
            } else {
                null
            }

        var isPageBreak = false

        for ((input, label) in elements) {
            val inputWidth =
                input.width(config.fontSize, baseFont)
                    ?: if (input.attr("type") == "radio" || input.attr("type") == "checkbox") {
                        config.fontSize
                    } else {
                        config.inputWidth
                    }
            val inputHeight = input.height(config.fontSize, baseFont)
            val labelWidth =
                label?.width(config.fontSize, baseFont) ?: if (input.attr("type") == "radio") {
                    baseFont.getWidthPoint(
                        input.attr("value").capitalizeFirst(),
                        config.fontSize,
                    ) + config.textRectPaddingX
                } else {
                    0f
                }
            val labelHeight =
                label?.height(config.fontSize, baseFont) ?: if (input.attr("type") == "radio") {
                    config.fontSize
                } else {
                    0f
                }

            val (labelRect, inputRect) =
                if (input.attr("type") == "radio" || input.attr("type") == "checkbox") {
                    val iRect =
                        getRectangleFor(inputWidth, inputHeight, 2 * config.innerObjectPadding, 0f, false) {
                            // page breaks literally break radio groups, so stop converting and just add what we have so far
                            acroForm.convertedNames.addAll(elements.map { it.first.id() })
                            writer.addAnnotation(radioGroup)
                            isPageBreak = true
                            true
                        }
                    if (isPageBreak) {
                        return // stop converting the rest of the radio group
                    }
                    getRectangleFor(labelWidth, labelHeight, 0f, config.innerObjectPadding) to iRect
                } else {
                    val lRect =
                        if (label != null) {
                            getRectangleFor(labelWidth, labelHeight, 0f, config.innerObjectPadding)
                        } else {
                            null
                        }
                    lRect to getRectangleFor(inputWidth, inputHeight, 0f)
                }

            if (label != null) {
                convertLabelField(label, document, writer, labelRect!!)
            } else if (input.attr("type") == "radio") {
                createLabel(input.attr("value").capitalizeFirst(), document, writer, labelRect!!)
            }

            if (radioGroup != null && input.attr("type") == "radio") {
                convertRadioField(input, acroForm, writer, radioGroup, inputRect)
            } else {
                convertInputField(input, acroForm, writer, inputRect)
            }
        }
        acroForm.convertedNames.addAll(elements.map { it.first.id() })
        if (elements.any {
                val type = it.first.attr("type")
                type == "radio" || type == "checkbox"
            }
        ) {
            forceNewLine(config.objectPadding - config.innerObjectPadding)
        }
        if (radioGroup != null) {
            writer.addAnnotation(radioGroup)
        }
    }

    private fun convertLabelField(
        element: HtmlElement,
        document: PdfDocument,
        writer: PdfWriter,
        rect: Rectangle,
    ) {
        createLabel(element.text(), document, writer, rect)
    }

    private fun createLabel(
        text: String,
        document: PdfDocument,
        writer: PdfWriter,
        rect: Rectangle,
    ) {
        val label = Paragraph(text, defaultFont)
        val columnText = ColumnText(writer.directContent)
        columnText.addText(label)
        columnText.setSimpleColumn(rect.llx, rect.lly, rect.urx, rect.ury)
        columnText.go()
    }

    private fun convertInputField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        rect: Rectangle? = null,
    ): PdfFormField? {
        // TODO: fix required and readonly, generally for all types
        when (element.attr("type").ifBlank { element.tagName() }) {
            "button" -> {
                // TODO: log warning: not supported
            }

            "checkbox" -> convertCheckboxField(element, form, writer, rect) // allow fieldset for multiple checkboxes
            "color" -> {
                // TODO: log warning: not supported
            }

            "date" -> convertDateField(element, form, writer, rect) // add min, max, step
            "datetime-local" -> convertDateTimeLocalField(element, form, writer, rect) // add min, max, step
            "email" ->
                convertEmailField(
                    element,
                    form,
                    writer,
                    rect,
                ) // add allowance for multiple email addresses if specified
            "file" -> convertFileField(element, form, writer, rect) // should be done
            "hidden" -> {
                // TODO: log warning: not supported
            } // For now, not supported
            "image" -> {
                // TODO: log warning: not supported
            } // For now, not supported
            "month" -> {
                // TODO: log warning: not supported
            } // For now, not supported, use date field with custom format instead
            "number" -> convertNumberField(element, form, writer, rect) // done
            "password" ->
                convertPasswordField(
                    element,
                    form,
                    writer,
                    rect,
                ) // maybe add password stars when not editing?
            "range" -> {
                // TODO: log warning: not supported
            } // TODO("Use text field with range slider")
            "reset" -> convertResetField(element, form, writer, rect) // done
            "search" -> {
                // TODO: log warning: not supported
            } // TODO("Use text field with search functionality")
            "submit" -> convertSubmitField(element, form, writer, rect) // add URL option
            "tel" -> {
                // TODO: log warning: not supported
            } // TODO("Use text field with telephone number formatting")
            "text" ->
                convertTextField(
                    element,
                    form,
                    writer,
                    false,
                    rect,
                )

            "textarea" -> convertTextField(element, form, writer, true, rect)

            "time" -> {
                // TODO: log warning: not supported
            } // TODO("Use text field with time formatting")
            "url" -> {
                // TODO: log warning: not supported
            } // TODO("Use text field with URL validation")
            "week" -> {
                // TODO: log warning: not supported
            } // TODO("Use text field with week formatting")
            else -> {
//                throw IllegalArgumentException("Unsupported input field type: $type")
            }
        }
        return null
    }

    private fun convertCheckboxField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        rect: Rectangle? = null,
    ): PdfFormField {
        val rectangle =
            rect?.copy(urx = rect.llx + rect.height) ?: getRectangleFor(
                config.fontSize,
                config.fontSize,
            )

        val mappingName = "${element.tagName()}-$currentElementIndex"
        val name = element.attr("name").ifBlank { element.attr("id") }.ifBlank { mappingName }

        return form
            .addCheckBox(
                name,
                element.attr("value"),
                element.hasAttr("checked"),
                rectangle.llx,
                rectangle.lly,
                rectangle.urx,
                rectangle.ury,
            ).apply {
                setMappingName(mappingName)
            }
    }

    private fun convertDateField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        rect: Rectangle? = null,
    ): PdfFormField = convertDateTimeField(element, form, writer, "dd.mm.yyyy", rect)

    private fun convertDateTimeLocalField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        rect: Rectangle? = null,
    ): PdfFormField = convertDateTimeField(element, form, writer, "dd.mm.yyyy HH:MM", rect)

    private fun convertMonthField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        rect: Rectangle? = null,
    ): PdfFormField = convertDateTimeField(element, form, writer, "mmm yyyy", rect)

    private fun convertTimeField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        rect: Rectangle? = null,
    ): PdfFormField = convertDateTimeField(element, form, writer, "MM:HH tt", rect)

    private fun convertWeekField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        rect: Rectangle? = null,
    ): PdfFormField {
        val field = convertTextField(element, form, writer, false, rect)

        val weekValidation =
            """
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
            val minValidation =
                """
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
        rect: Rectangle? = null,
    ): PdfFormField {
        val field = convertTextField(element, form, writer, false, rect)

        val format = element.attr("format").ifBlank { defaultFormat }

        field.setDefaultValueAsName(element.attr("value"))
        field.setAdditionalActions(PdfName.F, PdfAction.javaScript("AFDate_FormatEx(\"$format\");", writer))
        field.setAdditionalActions(PdfName.K, PdfAction.javaScript("AFDate_KeystrokeEx(\"$format\");", writer))

        if (element.hasAttr("min")) {
            // TODO: Test this, probably does not work
            val minValidation =
                """
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
            val maxValidation =
                """
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
            val stepValidation =
                """
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

    private fun convertEmailField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        rect: Rectangle? = null,
    ): PdfFormField {
        val field = convertTextField(element, form, writer, false, rect)

        val emailValidation =
            """
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

    private fun convertFileField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        rect: Rectangle? = null,
    ): PdfFormField {
        val field = convertTextField(element, form, writer, false, rect)

        field.addFlags(PdfFormField.FF_FILESELECT)

        return field
    }

    private fun convertNumberField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        rect: Rectangle? = null,
    ): PdfFormField {
        val field = convertTextField(element, form, writer, false, rect)

        val numberValidation =
            """
            var numberRegex = /^\d*$/;
            if (!event.willCommit && event.change && !global.isResettingForm) {
                event.rc = numberRegex.test(event.change);
            }
            """.trimIndent()
        field.setAdditionalActions(PdfName.K, PdfAction.javaScript(numberValidation, writer))

        // TODO: Handle list of suggested values

        if (element.hasAttr("min")) {
            val minValidation =
                """
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
            val maxValidation =
                """
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
            val base =
                if (element.hasAttr("min")) {
                    element.attr("min").toInt()
                } else if (element.hasAttr("value")) {
                    element.attr("value").toInt()
                } else {
                    element.attr("step").toInt()
                }
            val step = element.attr("step").toInt()

            val stepValidation =
                """
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

    private fun convertPasswordField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        rect: Rectangle? = null,
    ): PdfFormField {
        val field = convertTextField(element, form, writer, false, rect)

        field.addFlags(PdfFormField.FF_PASSWORD)

        return field
    }

    private fun convertRadioField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        group: PdfFormField,
        rect: Rectangle? = null,
    ): PdfFormField {
        val rectangle =
            rect?.copy(urx = rect.llx + rect.height) ?: getRectangleFor(
                config.fontSize,
                config.fontSize,
            )

        val mappingName = "${element.tagName()}-$currentElementIndex"

        val radio =
            RadioCheckField(writer, rectangle.toPdfRectangle(), null, element.attr("value"))
                .apply {
                    checkType = RadioCheckField.TYPE_CIRCLE
                    setMappingName(mappingName)
                }.fullField

        val name = group.get(PdfName.V).toString().substring(1)
        if (name == element.attr("value")) {
            radio.setAppearanceState(name)
        } else {
            radio.setAppearanceState("Off")
        }

        group.addKid(radio)

        return radio
    }

    private fun convertResetField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        rect: Rectangle? = null,
    ): PdfFormField {
        val rectangle =
            rect ?: getRectangleFor(
                50f,
                config.fontSize,
                0f,
                config.innerObjectPadding,
            )

        val mappingName = "${element.tagName()}-$currentElementIndex"
        val name = element.attr("name").ifBlank { element.attr("id") }.ifBlank { mappingName }
        val value = element.attr("value").ifBlank { "Reset" }

        val resetPushAction =
            """
            global.isResettingForm = true;
            """.trimIndent()

        val resetReleaseAction =
            """
            global.isResettingForm = false;
            """.trimIndent()

        return form
            .addResetButton(
                name,
                value,
                value,
                baseFont,
                config.fontSize,
                rectangle.llx,
                rectangle.lly,
                rectangle.urx,
                rectangle.ury,
            ).apply {
                setMappingName(mappingName)
                setAdditionalActions(PdfName.D, PdfAction.javaScript(resetPushAction, writer))
                setAdditionalActions(PdfName.BL, PdfAction.javaScript(resetReleaseAction, writer))
            }
    }

    private fun convertSubmitField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        rect: Rectangle? = null,
    ): PdfFormField {
        val rectangle =
            rect ?: getRectangleFor(
                50f,
                config.fontSize,
                0f,
                config.innerObjectPadding,
            )

        val mappingName = "${element.tagName()}-$currentElementIndex"
        val name = element.attr("name").ifBlank { element.attr("id") }.ifBlank { mappingName }
        val value = element.attr("value").ifBlank { "Submit" }

        return form
            .addHtmlPostButton(
                name,
                value,
                value,
                "", // TODO: Add URL
                baseFont,
                config.fontSize,
                rectangle.llx,
                rectangle.lly,
                rectangle.urx,
                rectangle.ury,
            ).apply {
                setMappingName(mappingName)
            }
    }

    private fun convertTextField(
        element: HtmlElement,
        form: PdfAcroForm,
        writer: PdfWriter,
        multiline: Boolean = false,
        rect: Rectangle? = null,
    ): PdfFormField {
        val width =
            if (element.hasAttr("size")) {
                val size = element.attr("size").toInt()
                config.innerObjectPadding + size * defaultFontWidth + config.innerObjectPadding
            } else {
                config.inputWidth
            }
        val height = config.fontSize

        val rectangle = rect ?: getRectangleFor(width, height, 0f, config.innerObjectPadding)

        /*
            Actions:
            - K: JavaScript keystroke action
            - V: JavaScript validation action
            - C: JavaScript calculation action
            - F: JavaScript format action
         */

        val mappingName = "${element.tagName().lowercase()}-$currentElementIndex"
        val name = element.attr("name").ifBlank { element.attr("id") }.ifBlank { mappingName }

        val field =
            if (multiline) {
                form.addMultiLineTextField(
                    name,
                    element.attr("placeholder"),
                    baseFont,
                    config.fontSize,
                    rectangle.llx,
                    rectangle.lly,
                    rectangle.urx,
                    rectangle.ury,
                )
            } else {
                form.addSingleLineTextField(
                    name,
                    element.attr("placeholder"),
                    baseFont,
                    config.fontSize,
                    rectangle.llx,
                    rectangle.lly,
                    rectangle.urx,
                    rectangle.ury,
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
            val validateMinLength =
                """
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
            val validatePattern =
                """
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
            field.addFlags(PdfFormField.FF_PASSWORD) // field is a password field (no change in display though)
            field.addFlags(PdfFormField.FF_READ_ONLY) // field is read-only
            field.put(PdfName.MAXLEN, PdfNumber(10)) // maximum length of text field (in characters, here: 10)

            field.addFlags(PdfFormField.FF_DONOTSPELLCHECK) // field should not be spell-checked
         */

        return field
    }
}
