package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.RGBColor
import de.l4zs.html2pdfform.backend.converter.convert
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.data.Rectangle
import de.l4zs.html2pdfform.backend.extension.height
import de.l4zs.html2pdfform.backend.extension.toPdfRectangle
import org.jsoup.nodes.Element

/**
 * Represents a fieldset. A fieldset is a group of form fields. It can have a legend that is shown as a title.
 * The fields are displayed below the legend.
 * The fieldset is drawn with a border around it to separate it from other fieldsets.
 *
 * @param element The HTML element that represents the fieldset.
 * @param context The context of the form.
 * @param id The ID of the fieldset.
 * @property fields The fields of the fieldset.
 * @property legend The legend of the fieldset.
 * @property isLastChildFieldset Whether the fieldset is the last child of its parent.
 * @property isFirstChildFieldset Whether the fieldset is the first child of its parent.
 */
class Fieldset(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
    val fields: List<FormField>,
) : FormField(FieldType.FIELDSET, element, context, id) {
    private val legend = element.selectFirst("legend")
    private val isLastChildFieldset =
        element.children().lastOrNull()?.tagName() == "fieldset" // to prevent double padding
    private val isFirstChildFieldset =
        element
            .children()
            .firstOrNull { it.tagName() != "legend" }
            ?.tagName() == "fieldset" // to prevent double padding

    init {
        val padding =
            (
                (if (isLastChildFieldset) 0 else 3) +
                    if (isFirstChildFieldset) -1 else 1
            ) * context.config.innerPaddingY
        rectangle =
            Rectangle(
                context.config.effectivePageWidth,
                padding +
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

        drawBorder(borderRectangle, title)

        if (title != null) {
            title.rectangle = title.rectangle.move(rectangle.llx, currentY - title.height)
            title.write()
            currentY -= title.height
            if (!isFirstChildFieldset) {
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

    private fun drawBorder(
        borderRectangle: Rectangle,
        title: Label?,
    ) {
        val cb = context.writer.directContent
        cb.rectangle(
            borderRectangle.toPdfRectangle().apply {
                borderColor = RGBColor(128, 128, 128)
                borderWidth = 1f
                border = com.lowagie.text.Rectangle.BOX
            },
        )
        if (title != null) {
            // draw white over border where title will be
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
    }
}

/**
 * Creates a fieldset.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The fieldset.
 */
suspend fun fieldset(
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
