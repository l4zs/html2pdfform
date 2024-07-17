package de.janniskramer.htmlform2pdfform.data

import com.lowagie.text.Rectangle

data class Rectangle(
    val llx: Float,
    val lly: Float,
    val urx: Float,
    val ury: Float,
) {
    constructor(width: Float, height: Float) : this(0f, 0f, width, height)

    val width: Float = urx - llx
    val height: Float = ury - lly

    fun move(
        x: Float,
        y: Float,
    ): Rectangle = Rectangle(llx + x, lly + y, urx + x, ury + y)

    fun scale(factor: Float): Rectangle = Rectangle(llx * factor, lly * factor, urx * factor, ury * factor)

    fun pad(padding: Float): Rectangle = Rectangle(llx - padding, lly - padding, urx + padding, ury + padding)

    fun pad(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
    ): Rectangle = Rectangle(llx - left, lly - bottom, urx + right, ury + top)

    fun toPdfRectangle(): Rectangle = Rectangle(llx, lly, urx, ury)
}
