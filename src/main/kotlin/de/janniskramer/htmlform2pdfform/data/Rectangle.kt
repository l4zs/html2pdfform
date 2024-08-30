package de.janniskramer.htmlform2pdfform.data

import com.lowagie.text.Rectangle as PdfRectangle

data class Rectangle(
    val llx: Float,
    val lly: Float,
    val urx: Float,
    val ury: Float,
) {
    constructor(width: Float, height: Float) : this(0f, 0f, width, height)

    val width: Float
        get() = urx - llx
    val height: Float
        get() = ury - lly

    fun move(
        x: Float,
        y: Float,
    ): Rectangle = Rectangle(llx + x, lly + y, urx + x, ury + y)

    fun pad(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
    ): Rectangle = Rectangle(llx - left, lly - bottom, urx + right, ury + top)

    fun toPdfRectangle(): PdfRectangle = PdfRectangle(llx, lly, urx, ury)
}
