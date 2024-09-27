package de.l4zs.html2pdfform.backend.extension

import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.data.Rectangle
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

fun Element.width(config: Config): Float =
    pureWidth(config).coerceAtMost(config.effectivePageWidth).let {
        if (this.tagName() == "label" && this.isLabelForBox()) {
            it.coerceAtMost(config.effectivePageWidth - config.boxSize - config.innerPaddingX)
        } else {
            it
        }
    }

private fun Element.pureWidth(config: Config) =
    if (this.tagName() == "input") {
        if (this.attr("type") == "checkbox" || this.attr("type") == "radio") {
            config.boxSize
        } else {
            config.inputWidth
        }
    } else if (this.tagName() in listOf("textarea", "select", "signature", "button")) {
        config.inputWidth
    } else {
        config.baseFont.getWidthPoint(this.text(), config.fontSize) + 0.01f
    }

fun Element.height(config: Config): Float = pureHeight(config).coerceAtMost(config.effectivePageHeight)

private fun Element.pureHeight(config: Config): Float =
    if (this.tagName() == "select" && this.hasAttr("multiple")) {
        (this.attr("size").toIntOrNull() ?: config.selectSize) * (config.fontSize + config.innerPaddingY)
    } else if (this.tagName() == "textarea") {
        (this.attr("rows").toIntOrNull() ?: config.textareaRows) * (config.fontSize + config.innerPaddingY)
    } else if (this.tagName() == "input" && (this.attr("type") == "checkbox" || this.attr("type") == "radio")) {
        config.boxSize
    } else if (this.tagName() == "signature") {
        (config.fontSize + config.innerPaddingY) * 2
    } else if (this.tagName() == "label") {
        config.fontSize * ceil(pureWidth(config) / config.effectivePageWidth)
    } else {
        config.fontSize + config.innerPaddingY
    }

fun Element.defaultRectangle(config: Config): Rectangle = Rectangle(width(config), height(config))
