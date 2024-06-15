package de.janniskramer.htmlform2pdfform.converter

import com.lowagie.text.Rectangle

data class Rectangle(
    val llx: Float,
    val lly: Float,
    val urx: Float,
    val ury: Float,
) {
    val width: Float = urx - llx
    val height: Float = ury - lly

    fun toPdfRectangle(): Rectangle = Rectangle(llx, lly, urx, ury)
}
