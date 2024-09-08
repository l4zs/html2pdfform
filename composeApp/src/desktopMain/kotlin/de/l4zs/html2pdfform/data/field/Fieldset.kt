package de.l4zs.html2pdfform.data.field

import com.lowagie.text.pdf.RGBColor
import de.l4zs.html2pdfform.ui.config
import de.l4zs.html2pdfform.converter.convert
import de.l4zs.html2pdfform.data.Context
import de.l4zs.html2pdfform.data.Rectangle
import de.l4zs.html2pdfform.extension.height
import org.jsoup.nodes.Element

class Fieldset(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
    val fields: List<FormField>,
) : FormField(FieldType.FIELDSET, element, context, id) {
    private val legend = element.selectFirst("legend")
    private val isLastChildFieldset = element.children().last()?.tagName() == "fieldset"

    init {
        rectangle =
            Rectangle(
                config.effectivePageWidth,
                ((if (isLastChildFieldset) 0 else 3) + 1) * config.innerPaddingY +
                    (legend?.height() ?: 0f) +
                    fields.sumOf { it.height + config.innerPaddingY.toDouble() }.toFloat(),
            )
    }

    override fun write() {
        var currentY = rectangle.lly + rectangle.height - config.innerPaddingY

        val title: Label? =
            if (legend != null) {
                fakeLabel(context, legend.text())
            } else {
                null
            }

        val borderRectangle =
            if (title != null) {
                rectangle.pad(
                    2 * config.innerPaddingX,
                    -title.height / 2 - config.innerPaddingY,
                    2 * config.innerPaddingX,
                    0f,
                )
            } else {
                rectangle.pad(
                    2 * config.innerPaddingX,
                    0f,
                    2 * config.innerPaddingX,
                    -config.innerPaddingY,
                )
            }

        val cb = context.writer.directContent
        cb.rectangle(
            borderRectangle.toPdfRectangle().apply {
                borderColor = RGBColor(128, 128, 128)
                borderWidth = 1f
                border = com.lowagie.text.Rectangle.BOX
            },
        )
        if (title != null) {
            cb.setColorStroke(RGBColor(255, 255, 255))
            cb.moveTo(
                borderRectangle.llx + config.innerPaddingX,
                borderRectangle.ury,
            )
            cb.lineTo(
                borderRectangle.llx + 2 * config.innerPaddingX + title.width + config.innerPaddingX,
                borderRectangle.ury,
            )
            cb.stroke()
        }
        cb.sanityCheck()

        if (title != null) {
            title.rectangle = title.rectangle.move(rectangle.llx, currentY - title.height)
            title.write()
            currentY -= title.height + 2 * config.innerPaddingY
        }

        fields.forEach {
            it.rectangle =
                it.rectangle.move(
                    rectangle.llx,
                    currentY - it.height,
                )
            currentY -= it.height + config.innerPaddingY
            it.write()
        }
    }
}

fun fieldset(
    element: Element,
    context: Context,
): Fieldset {
    val fields =
        element
            .children()
            .filterNot { it.tagName() == "legend" }
            .mapNotNull { it.convert(context) }
            .flatten()
    val field = Fieldset(element, context, fields = fields)
    return field
}
