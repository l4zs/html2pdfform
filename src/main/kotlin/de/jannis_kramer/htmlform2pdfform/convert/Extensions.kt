package de.jannis_kramer.htmlform2pdfform.convert

import com.lowagie.text.pdf.BaseFont
import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.PdfName
import org.jsoup.nodes.Element

fun Int.pad(length: Int): String {
    return this.toString().padStart(length, '0')
}

fun String.indentLines(length: Int = 4): String {
    return this.lines().joinToString("\n" + " ".repeat(length))
}

fun String.capitalizeFirst(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

val PdfFormField.mappingName: String
    get() = this.getAsString(PdfName.TM).toString()

fun Element.findLabelFor(id: String): Element? {
    return this.select("label[for=$id]").firstOrNull()
}

fun Element.findRadioGroupFor(name: String): List<Element> {
    return this.select("input[type=radio][name=$name]")
}

fun Element.width(fontSize: Float, baseFont: BaseFont): Float? {
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

fun Element.height(fontSize: Float, baseFont: BaseFont): Float {
    val margin = fontSize / 4
    return if (this.attr("height").isNotEmpty()) {
        this.attr("height").toFloat() + margin
    } else if (this.tagName() == "textarea") {
        this.attr("rows").toInt() * (fontSize + margin)
    } else {
        fontSize + margin
    }
}
