package de.janniskramer.htmlform2pdfform.extensions

import de.janniskramer.htmlform2pdfform.Config
import org.jsoup.nodes.Element

fun Element.form(): Element {
    var parent = this.parent()
    while (parent != null && parent.tagName() != "form") {
        parent = parent.parent()
    }
    return parent ?: this
}

fun Element.findLabel(): Element? = this.form().select("label[for=${id()}]").firstOrNull()

fun Element.findRadiosInGroup(): List<Element> {
    val name = this.attr("name")
    return this.form().select("input[type=radio][name=$name]")
}

fun Element.findCheckedRadioInGroup(): Element {
    val name = this.attr("name")
    return this.form().select("input[type=radio][name='$name'][checked]").first() ?: this
}

fun Element.findOptions(): List<Element> = this.select("option")

fun Element.width(): Float =
    if (this.tagName() == "input" && this.hasAttr("size")) {
        val fontWidth = Config.baseFont.getWidthPoint("X", Config.fontSize)
        this.attr("size").toInt() * fontWidth + Config.textRectPaddingX
    } else if (this.tagName() == "input") {
        if (this.attr("type") == "checkbox" || this.attr("type") == "radio") {
            Config.boxSize
        } else {
            Config.inputWidth
        }
    } else if (this.tagName() == "textarea") {
        Config.inputWidth
    } else {
        Config.baseFont.getWidthPoint(this.text(), Config.fontSize) + Config.textRectPaddingX
    }

fun Element.height(): Float =
    if (this.tagName() == "select" && this.hasAttr("size")) {
        (this.attr("size").toIntOrNull() ?: Config.selectSize) * Config.fontSize
    } else if (this.tagName() == "textarea") {
        (this.attr("rows").toIntOrNull() ?: Config.textareaRows) * Config.fontSize
    } else if (this.tagName() == "input" && (this.attr("type") == "checkbox" || this.attr("type") == "radio")) {
        Config.boxSize
    } else {
        Config.fontSize
    }
