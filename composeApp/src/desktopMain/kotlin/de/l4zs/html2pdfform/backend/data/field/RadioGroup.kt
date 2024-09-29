package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.data.Rectangle
import de.l4zs.html2pdfform.backend.extension.findCheckedRadioInGroup
import de.l4zs.html2pdfform.backend.extension.findRadiosInGroup
import de.l4zs.html2pdfform.backend.util.Actions
import de.l4zs.html2pdfform.backend.util.calculateRadiosPerRow
import org.jsoup.nodes.Element

/**
 * A radio group is a group of radio buttons that are mutually exclusive,
 * meaning that only one radio button can be selected at a time.
 *
 * @param element The element that represents the radio group.
 * @param context The context of the PDF document.
 * @param id The ID of the radio group.
 * @param radioGroup The radio group field.
 * @property groupName The name of the radio group.
 * @property radios The radio buttons in the group.
 * @property radiosPerRow The number of radio buttons per row.
 * @property spreadEvenWidth The width that each radio button should have.
 * @property rowHeights The heights of the resulting rows.
 */
class RadioGroup(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
    radioGroup: PdfFormField,
    private val groupName: String,
    private val radios: List<FieldWithLabel<Radio>>,
) : FormField(FieldType.RADIO_GROUP, element, context, id) {
    private val radiosPerRow = calculateRadiosPerRow(radios.size, radios.maxOf { it.width }, context.config)
    private val spreadEvenWidth = context.config.effectivePageWidth / radiosPerRow
    private val rowHeights =
        radios
            .chunked(radiosPerRow)
            .map { row -> row.maxOf { it.height } + context.config.innerPaddingY }

    init {
        field = radioGroup
        rectangle =
            Rectangle(
                context.config.effectivePageWidth,
                rowHeights.sum() - context.config.innerPaddingY,
            )
        additionalActions[PdfFormField.AA_JS_CHANGE]!!.add(Actions.RadioGroup.toggleFields(name ?: ""))
    }

    override fun write() {
        setAdditionalActions()
        radios.forEachIndexed { index, fieldWithLabel ->
            val rowIndex = index / radiosPerRow
            val indexInRow = index % radiosPerRow
            fieldWithLabel.rectangle =
                fieldWithLabel.rectangle.move(
                    rectangle.llx + indexInRow * spreadEvenWidth,
                    rectangle.ury - rowHeights.take(rowIndex + 1).sum(),
                )
            fieldWithLabel.write()
            field.addKid(fieldWithLabel.formField.field)
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

        context.acroForm.addRadioGroup(field)
    }
}

/**
 * Creates a radio group form field.
 * The radio group is a group of radio buttons that are mutually exclusive,
 * meaning that only one radio button can be selected at a time.
 * The radio group is created based on the given HTML element and context, while all radio buttons are created as well.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The radio group.
 */
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
