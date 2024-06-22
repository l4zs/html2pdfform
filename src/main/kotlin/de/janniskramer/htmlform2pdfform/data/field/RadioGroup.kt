package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.Config
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.extensions.findCheckedRadioInGroup
import de.janniskramer.htmlform2pdfform.extensions.findRadiosInGroup
import org.jsoup.nodes.Element

class RadioGroup(
    element: Element,
    id: Int,
    private val radioGroup: PdfFormField,
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

    private val radiosPerRow = (Config.effectivePageWidth / maxWidth).toInt()

    val height = (radios.size / radiosPerRow) * (Config.fontHeight + Config.innerPaddingY) - Config.innerPaddingY

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
                            context.locationHandler.padX(maxWidth - radio.width)
                            context.locationHandler.padX(2 * Config.innerPaddingX)
                        }
                    }.also {
                        context.locationHandler.newLine()
                        if (size > 0) {
                            context.locationHandler.padY(Config.innerPaddingY)
                        }
                    }
            }.forEach { radioGroup.addKid(it) }

        context.acroForm.addRadioGroup(radioGroup)

        return radioGroup
    }
}

fun FormFields.radioGroup(
    element: Element,
    context: Context,
): RadioGroup {
    val name = element.attr("name")

    val radioGroup =
        context.acroForm.getRadioGroup(
            name,
            element.findCheckedRadioInGroup().attr("value"),
            false,
        )

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
        radios,
    )
}
