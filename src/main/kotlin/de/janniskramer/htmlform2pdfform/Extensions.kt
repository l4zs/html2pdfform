package de.janniskramer.htmlform2pdfform

import com.lowagie.text.pdf.BaseFont
import org.jsoup.nodes.Element

fun String.capitalizeFirst(): String = this.lowercase().replaceFirstChar { it.uppercase() }

fun Element.findLabelFor(id: String): Element? {
    if (id.isNullOrEmpty()) return null
    return this.select("label[for=$id]").firstOrNull()
}

fun Element.findRadioGroupFor(name: String): List<Element> = this.select("input[type=radio][name=$name]")

fun Element.width(
    fontSize: Float,
    baseFont: BaseFont,
): Float? {
    val fontWidth = baseFont.getWidthPoint("X", fontSize)
    return if (this.attr("width").isNotEmpty()) {
        this.attr("width").toFloat()
    } else if (this.tagName() == "input" && this.hasAttr("size")) {
        this.attr("size").toInt() * fontWidth
    } else if (this.tagName() == "label" || this.tagName() == "legend") {
        baseFont.getWidthPoint(this.text(), fontSize) + 0.1f // Add a small margin so the text does not break lines
    } else {
        null
    }
}

fun Element.height(
    fontSize: Float,
    baseFont: BaseFont,
): Float {
    val margin = fontSize / 4
    return if (this.attr("height").isNotEmpty()) {
        this.attr("height").toFloat() + margin
    } else if (this.tagName() == "textarea") {
        this.attr("rows").toInt() * (fontSize + margin)
    } else {
        fontSize + margin
    }
}
