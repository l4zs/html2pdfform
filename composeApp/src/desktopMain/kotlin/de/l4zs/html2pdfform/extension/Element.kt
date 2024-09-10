package de.l4zs.html2pdfform.extension

import de.l4zs.html2pdfform.config.*
import de.l4zs.html2pdfform.data.Rectangle
import org.jsoup.nodes.Element
import kotlin.math.ceil

fun Element.form(): Element {
    var parent = this.parent()
    while (parent != null && parent.tagName() != "form") {
        parent = parent.parent()
    }
    return parent ?: this
}

fun Element.findLabel(): Element? {
    if (id().isBlank()) {
        return null
    }
    return this.form().select("label[for=${id()}]").firstOrNull()
}

fun Element.isLabelForBox(): Boolean {
    if (this.tagName() != "label" || this.attr("for").isBlank()) {
        return false
    }
    val input = this.form().select("input[id=${this.attr("for")}]").firstOrNull() ?: return false
    return input.attr("type") == "checkbox" || input.attr("type") == "radio"
}

fun Element.findRadiosInGroup(): List<Element> {
    if (this.tagName() != "input" || this.attr("type") != "radio" || this.attr("name").isBlank()) {
        return emptyList()
    }
    val name = this.attr("name")
    return this.form().select("input[type=radio][name=$name]")
}

fun Element.findCheckedRadioInGroup(): Element {
    if (this.attr("name").isBlank()) {
        return this
    }
    val name = this.attr("name")
    return this.form().select("input[type=radio][name='$name'][checked]").first() ?: this
}

fun Element.findOptions(): List<Element> = this.select("option")

fun Element.width(): Float {
    return pureWidth().coerceAtMost(config.effectivePageWidth).let {
        if (this.tagName() == "label" && this.isLabelForBox()) {
            it.coerceAtMost(config.effectivePageWidth - config.boxSize - config.innerPaddingX)
        } else {
            it
        }
    }
}

private fun Element.pureWidth() =
    if (this.tagName() == "input" && this.hasAttr("size")) {
        val fontWidth = config.baseFont.getWidthPoint("X", config.fontSize)
        this.attr("size").toInt() * fontWidth + config.textRectPadding
    } else if (this.tagName() == "input") {
        if (this.attr("type") == "checkbox" || this.attr("type") == "radio") {
            config.boxSize
        } else {
            config.inputWidth
        }
    } else if (this.tagName() == "textarea" || this.tagName() == "select" || this.tagName() == "signature") {
        config.inputWidth
    } else {
        config.baseFont.getWidthPoint(this.text(), config.fontSize) + config.textRectPadding
    }

fun Element.height(): Float = pureHeight().coerceAtMost(config.effectivePageHeight)

private fun Element.pureHeight(): Float =
    if (this.tagName() == "select" && this.hasAttr("multiple") && (this.attr("size").toIntOrNull() ?: 0) > 1) {
        (this.attr("size").toIntOrNull() ?: config.selectSize) * (config.fontSize + config.innerPaddingY)
    } else if (this.tagName() == "textarea") {
        (this.attr("rows").toIntOrNull() ?: config.textareaRows) * (config.fontSize + config.innerPaddingY)
    } else if (this.tagName() == "input" && (this.attr("type") == "checkbox" || this.attr("type") == "radio")) {
        config.boxSize
    } else if (this.tagName() == "signature") {
        (config.fontSize + config.innerPaddingY) * 2
    } else if (this.tagName() == "label") {
        config.fontSize * ceil(pureWidth() / config.effectivePageWidth)
    } else {
        config.fontSize + config.innerPaddingY
    }

fun Element.defaultRectangle(): Rectangle = Rectangle(width(), height())
