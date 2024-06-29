package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.Config
import de.janniskramer.htmlform2pdfform.data.Actions
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.extensions.findCheckedRadioInGroup
import de.janniskramer.htmlform2pdfform.extensions.findRadiosInGroup
import org.jsoup.nodes.Element
import kotlin.math.ceil

class RadioGroup(
    element: Element,
    id: Int,
    private val radioGroup: PdfFormField,
    private val groupName: String,
    private val radios: List<FieldWithLabel<Radio>>,
) : FormField(FieldType.RADIO_GROUP, element, id) {
    private val maxWidth: Float
        get() {
            val maxLabelWidth =
                radios.maxOfOrNull {
                    Config.baseFont.getWidthPoint(
                        it.label?.element?.text(),
                        Config.fontSize,
                    )
                } ?: 0f
            return Config.boxSize + Config.innerPaddingX + maxLabelWidth + 2 * Config.innerPaddingX
        }

    private val radiosPerRow =
        (Config.effectivePageWidth / maxWidth)
            .toInt()
            .coerceAtLeast(1)
            .coerceAtMost(Config.maxRadiosPerRow)
            .coerceAtMost(radios.size)

    private val padding = Config.effectivePageWidth / radiosPerRow

    val height =
        ceil((radios.size.toFloat() / radiosPerRow)) * (Config.fontHeight + Config.innerPaddingY) - Config.innerPaddingY

    override fun write(context: Context): PdfFormField {
        var size = radios.size
        if (!context.locationHandler.wouldFitOnPageY(height)) {
            context.locationHandler.newPage()
        }

        radios
            .windowed(radiosPerRow, radiosPerRow, true)
            .flatMap { row ->
                row
                    .map { radio ->
                        radio.write(context).also {
                            size--
                            context.locationHandler.padX(padding - radio.width)
                            context.locationHandler.padX(2 * Config.innerPaddingX)
                        }
                    }.also {
                        context.locationHandler.newLine()
                        if (size > 0) {
                            context.locationHandler.padY(Config.innerPaddingY)
                        }
                    }
            }.forEach { radioGroup.addKid(it) }

        val toggles =
            radios.associate {
                it.element.attr("value").ifBlank { it.id.toString() } to it.toggles
            }
        context.writer.addJavaScript(
            Actions.RadioGroup.toggleFields(toggles, radios.first().name!!, groupName),
        )

        context.acroForm.addRadioGroup(radioGroup)

        return radioGroup
    }
}

fun FormFields.radioGroup(
    element: Element,
    context: Context,
): RadioGroup {
    val name = element.attr("name") + "-rg${context.currentElementIndex}"
    val checked = element.findCheckedRadioInGroup().attr("value")

    val radioGroup =
        context.acroForm
            .getRadioGroup(
                name,
                checked,
                false,
            ).apply {
                setDefaultValueAsName(checked)
            }

    val radios =
        element
            .findRadiosInGroup()
            .map {
                FormFields.radio(it, context, radioGroup)
            }.map {
                FieldWithLabel(it, it.label(context) ?: FormFields.fakeLabel(context, it.element.attr("value")))
            }

    context.radioGroups[name] = radioGroup

    return RadioGroup(
        element,
        context.currentElementIndex,
        radioGroup,
        name,
        radios,
    )
}
