package de.l4zs.html2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.config.config
import de.l4zs.html2pdfform.config.effectivePageWidth
import de.l4zs.html2pdfform.data.Context
import de.l4zs.html2pdfform.data.Rectangle
import de.l4zs.html2pdfform.extension.findCheckedRadioInGroup
import de.l4zs.html2pdfform.extension.findRadiosInGroup
import de.l4zs.html2pdfform.util.Actions
import de.l4zs.html2pdfform.util.calculateRadiosPerRow
import org.jsoup.nodes.Element

class RadioGroup(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
    private val radioGroup: PdfFormField,
    private val groupName: String,
    private val radios: List<FieldWithLabel<Radio>>,
) : FormField(FieldType.RADIO_GROUP, element, context, id) {
    private val radiosPerRow = calculateRadiosPerRow(radios.size, radios.maxOf { it.width })
    private val spreadEvenWidth = config.effectivePageWidth / radiosPerRow
    private val rowHeights =
        radios
            .chunked(radiosPerRow)
            .map { row -> row.maxOf { it.height } + config.innerPaddingY }

    init {
        rectangle =
            Rectangle(
                config.effectivePageWidth,
                rowHeights.sum() - config.innerPaddingY,
            )
    }

    override fun write() {
        radios.forEachIndexed { index, fieldWithLabel ->
            val rowIndex = index / radiosPerRow
            val indexInRow = index % radiosPerRow
            fieldWithLabel.rectangle =
                fieldWithLabel.rectangle.move(
                    rectangle.llx + indexInRow * spreadEvenWidth,
                    rectangle.ury - rowHeights.take(rowIndex + 1).sum(),
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
