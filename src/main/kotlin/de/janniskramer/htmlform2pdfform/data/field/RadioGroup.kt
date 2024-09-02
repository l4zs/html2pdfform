package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.config
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.data.Rectangle
import de.janniskramer.htmlform2pdfform.extension.findCheckedRadioInGroup
import de.janniskramer.htmlform2pdfform.extension.findRadiosInGroup
import de.janniskramer.htmlform2pdfform.util.Actions
import org.jsoup.nodes.Element
import kotlin.math.ceil

class RadioGroup(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
    private val radioGroup: PdfFormField,
    private val groupName: String,
    private val radios: List<FieldWithLabel<Radio>>,
) : FormField(FieldType.RADIO_GROUP, element, context, id) {
    private val maxWidth = radios.maxOf { it.width }
    private val maxHeight = radios.maxOf { it.height } // should generally be the same for all radios

    private val radiosPerRow =
        (config.effectivePageWidth / (maxWidth + 3 * config.innerPaddingX))
            .toInt()
            .coerceAtLeast(1)
            .coerceAtMost(config.maxRadiosPerRow)
            .coerceAtMost(radios.size)

    // the width that the radios should take up
    private val effectiveRadioRect =
        Rectangle(
            config.effectivePageWidth / radiosPerRow,
            maxHeight,
        )

    init {
        rectangle =
            Rectangle(
                config.effectivePageWidth,
                ceil(radios.size.toFloat() / radiosPerRow) * (maxHeight + config.innerPaddingY) - config.innerPaddingY,
            )
    }

    override fun write() {
        radios.forEachIndexed { index, fieldWithLabel ->
            fieldWithLabel.rectangle =
                fieldWithLabel.rectangle.move(
                    rectangle.llx + (index % radiosPerRow) * effectiveRadioRect.width,
                    rectangle.ury - ((index / radiosPerRow) + 1) * (maxHeight + config.innerPaddingY),
                )
            fieldWithLabel.write()
            radioGroup.addKid(fieldWithLabel.formField.field)
        }

        val toggles =
            radios.associate {
                it.formField.element
                    .attr("value")
                    .ifBlank { it.id.toString() } to it.toggles
            }
        context.writer.addJavaScript(
            Actions.RadioGroup.toggleFields(toggles, radios.first().name!!, groupName),
        )

        context.acroForm.addRadioGroup(radioGroup)
    }
}

fun radioGroup(
    element: Element,
    context: Context,
): RadioGroup {
    val id = context.currentElementIndex
    val name = element.attr("name") + "-rg$id"
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
                radio(it, context, radioGroup)
            }.map {
                FieldWithLabel(it, it.label() ?: fakeLabel(context, it.element.attr("value")), context)
            }

    context.radioGroups[element.attr("name")] = radioGroup

    return RadioGroup(
        element,
        context,
        id,
        radioGroup,
        name,
        radios,
    )
}
