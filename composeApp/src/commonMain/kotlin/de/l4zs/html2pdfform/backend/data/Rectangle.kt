package de.l4zs.html2pdfform.backend.data

/**
 * A rectangle with a lower left corner and an upper right corner.
 *
 * @property llx The x-coordinate of the lower left corner.
 * @property lly The y-coordinate of the lower left corner.
 * @property urx The x-coordinate of the upper right corner.
 * @property ury The y-coordinate of the upper right corner.
 */
open class Rectangle(
    val llx: Float,
    val lly: Float,
    val urx: Float,
    val ury: Float,
) {
    /**
     * Creates a rectangle with the given width and height.
     *
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     */
    constructor(width: Float, height: Float) : this(0f, 0f, width, height)

    val width: Float
        get() = urx - llx
    val height: Float
        get() = ury - lly

    /**
     * Moves the rectangle by the given x and y values.
     *
     * @param x The x value to move the rectangle by.
     * @param y The y value to move the rectangle by.
     * @return The moved rectangle.
     */
    fun move(
        x: Float,
        y: Float,
    ): Rectangle = Rectangle(llx + x, lly + y, urx + x, ury + y)

    /**
     * Pads the rectangle by the given values.
     *
     * @param left The left padding.
     * @param top The top padding.
     * @param right The right padding.
     * @param bottom The bottom padding.
     * @return The padded rectangle.
     */
    fun pad(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
    ): Rectangle = Rectangle(llx - left, lly - bottom, urx + right, ury + top)
}
