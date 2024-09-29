package de.l4zs.html2pdfform.backend.extension

import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.data.Rectangle
import org.jsoup.nodes.Element
import kotlin.math.ceil

/**
 * Returns the form of the element
 *
 * @return the form element or the element itself if it is a form element
 * @receiver an [Element]
 */
fun Element.form(): Element {
    if (this.tagName() == "form") {
        return this
    }
    if (hasAttr("form")) {
        return select("form[id=${attr("form")}]").firstOrNull() ?: this
    }
    return parent()?.form() ?: this
}

/**
 * Finds the label for the element
 *
 * @return the label element or null if no label was found
 * @receiver an [Element]
 */
fun Element.findLabel(): Element? {
    if (id().isBlank()) {
        return null
    }
    return this.form().select("label[for=${id()}]").firstOrNull()
}

/**
 * Checks if the element is a label for a checkbox or radio button
 *
 * @return true if the element is a label for a checkbox or radio button,
 *    false otherwise
 * @receiver an [Element]
 */
fun Element.isLabelForBox(): Boolean {
    if (this.tagName() != "label" || this.attr("for").isBlank()) {
        return false
    }
    val input = this.form().select("input[id=${this.attr("for")}]").firstOrNull() ?: return false
    return input.attr("type") == "checkbox" || input.attr("type") == "radio"
}

/**
 * Finds all the radios in the same group as the element
 *
 * @return a list of radio elements in the same group as the element
 * @receiver an [Element] that is a radio button
 */
fun Element.findRadiosInGroup(): List<Element> {
    if (this.tagName() != "input" || this.attr("type") != "radio" || this.attr("name").isBlank()) {
        return emptyList()
    }
    val name = this.attr("name")
    return this.form().select("input[type=radio][name=$name]")
}

/**
 * Finds the checked radio in the same group as the element
 *
 * @return the checked radio element in the same group as the element
 * @receiver an [Element] that is a radio button
 */
fun Element.findCheckedRadioInGroup(): Element {
    if (this.attr("name").isBlank()) {
        return this
    }
    val name = this.attr("name")
    return this.form().select("input[type=radio][name='$name'][checked]").first() ?: this
}

/**
 * Finds the options of the element
 *
 * @return a list of option elements
 * @receiver an [Element] that is a select element
 */
fun Element.findOptions(): List<Element> = this.select("option")

/**
 * Calculates the width of the element
 *
 * @param config the configuration
 * @return the width of the element
 * @receiver an [Element]
 */
fun Element.width(config: Config): Float =
    pureWidth(config).coerceAtMost(config.effectivePageWidth).let {
        if (this.tagName() == "label" && this.isLabelForBox()) {
            it.coerceAtMost(config.effectivePageWidth - config.boxSize - config.innerPaddingX)
        } else {
            it
        }
    }

/**
 * Calculates the pure width of the element. The pure width is the width of
 * the element without any constraints
 *
 * @param config the configuration
 * @return the pure width of the element
 * @receiver an [Element]
 */
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

/**
 * Calculates the height of the element
 *
 * @param config the configuration
 * @return the height of the element
 * @receiver an [Element]
 */
fun Element.height(config: Config): Float = pureHeight(config).coerceAtMost(config.effectivePageHeight)

/**
 * Calculates the pure height of the element. The pure height is the height
 * of the element without any constraints
 *
 * @param config the configuration
 * @return the pure height of the element
 * @receiver an [Element]
 */
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

/**
 * Calculates the default rectangle of the element based on the
 * configuration settings
 *
 * @param config the configuration
 * @return the default rectangle of the element
 * @receiver an [Element]
 */
fun Element.defaultRectangle(config: Config): Rectangle = Rectangle(width(config), height(config))
