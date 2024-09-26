package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.RGBColor
import de.l4zs.html2pdfform.backend.converter.convert
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.data.Rectangle
import de.l4zs.html2pdfform.backend.extension.height
import de.l4zs.html2pdfform.backend.extension.toPdfRectangle
import org.jsoup.nodes.Element

class Fieldset(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
    val fields: List<FormField>,
) : FormField(FieldType.FIELDSET, element, context, id) {
    private val legend = element.selectFirst("legend")
    private val isLastChildFieldset =
        element.children().lastOrNull()?.tagName() == "fieldset" // to prevent double padding
    private val isFirtChildFieldset =
        element
            .children()
            .firstOrNull { it.tagName() != "legend" }
            ?.tagName() == "fieldset" // to prevent double padding

    init {
        rectangle =
            Rectangle(
                context.config.effectivePageWidth,
                (
                    (if (isLastChildFieldset) 0 else 3) +
                        if (isFirtChildFieldset) -1 else 1
                ) * context.config.innerPaddingY +
                    (legend?.height(context.config) ?: 0f) +
                    fields.sumOf { it.height + context.config.innerPaddingY.toDouble() }.toFloat(),
            )
    }

    override fun write() {
        var currentY = rectangle.lly + rectangle.height - context.config.innerPaddingY

        val title: Label? =
            if (legend != null) {
                fakeLabel(context, legend.text())
            } else {
                null
            }

        val borderRectangle =
            if (title != null) {
                rectangle.pad(
                    2 * context.config.innerPaddingX,
                    -title.height / 2 - context.config.innerPaddingY,
                    2 * context.config.innerPaddingX,
                    0f,
                )
            } else {
                rectangle.pad(
                    2 * context.config.innerPaddingX,
                    0f,
                    2 * context.config.innerPaddingX,
                    -context.config.innerPaddingY,
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
            cb.setLineWidth(2f)
            cb.moveTo(
                borderRectangle.llx + context.config.innerPaddingX,
                borderRectangle.ury,
            )
            cb.lineTo(
                borderRectangle.llx + 2 * context.config.innerPaddingX + title.width + context.config.innerPaddingX,
                borderRectangle.ury,
            )
            cb.stroke()
        }
        cb.sanityCheck()

        if (title != null) {
            title.rectangle = title.rectangle.move(rectangle.llx, currentY - title.height)
            title.write()
            currentY -= title.height
            if (!isFirtChildFieldset) {
                currentY -= 2 * context.config.innerPaddingY
            }
        }

        fields.forEach {
            it.rectangle =
                it.rectangle.move(
                    rectangle.llx,
                    currentY - it.height,
                )
            currentY -= it.height + context.config.innerPaddingY
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
