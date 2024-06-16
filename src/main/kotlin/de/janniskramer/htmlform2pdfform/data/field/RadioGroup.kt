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
    override fun write(context: Context): PdfFormField {
        // TODO: page break handling

        radios
            .map {
                val radio = it.write(context)
                context.locationHandler.newLine()
                context.locationHandler.padY(Config.innerPaddingY)
                radio
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
