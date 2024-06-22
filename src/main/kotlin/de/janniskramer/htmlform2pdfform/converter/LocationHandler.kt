package de.janniskramer.htmlform2pdfform.converter

import com.lowagie.text.Document
import de.janniskramer.htmlform2pdfform.Config
import de.janniskramer.htmlform2pdfform.data.Rectangle

/**
 * Location handler for placing elements on the page. The location handler
 * keeps track of the current position on the page and provides methods to
 * calculate the position of the next element.
 *
 * @constructor Create empty Location handler
 * @property pdf The PDF document to place elements on
 */
class LocationHandler(
    private val pdf: Document,
) {
    var currentX = Config.pageMinX
        private set
    var currentY = Config.pageMaxY
        private set
    private var lastLineHeight = 0f // needed for new line calculation

    /** Create new page and reset position */
    fun newPage() {
        pdf.newPage()
        currentX = Config.pageMinX
        currentY = Config.pageMaxY
    }

    /**
     * Return an element with the given width would fit on the current page
     *
     * @param width The width of the element
     * @return true if an element with the given width would fit, false
     *     otherwise
     */
    fun wouldFitOnPageX(width: Float): Boolean = currentX + width <= Config.pageMaxX

    /**
     * Return an element with the given height would fit on the current page
     *
     * @param height The height of the element
     * @return true if an element with the given height would fit, false
     *     otherwise
     */
    fun wouldFitOnPageY(height: Float): Boolean = currentY - height >= Config.pageMinY

    /**
     * Returns a rectangle for an element starting at the current position with
     * the given [width] and [height]. Automatically creates new pages and
     * lines if necessary.
     *
     * @param width The width of the element
     * @param height The height of the rectangle
     * @return A rectangle representing the position of the element
     */
    fun getRectangleFor(
        width: Float,
        height: Float,
    ): Rectangle {
        if (!wouldFitOnPageY(height)) {
            newPage()
        }
        if (!wouldFitOnPageX(width)) {
            newLine()
        }
        lastLineHeight = lastLineHeight.coerceAtLeast(height)

        val llx = currentX
        val lly = currentY - height
        val urx = llx + width
        val ury = lly + height
        val rectangle = Rectangle(llx, lly, urx, ury)

        currentX = urx
        return rectangle
    }

    /**
     * Add a padding to the current position
     *
     * @param width The width of the padding
     */
    fun padX(width: Float) {
        currentX += width
    }

    /**
     * Add a padding to the current position
     *
     * @param height The height of the padding
     */
    fun padY(height: Float) {
        currentY -= height
    }

    /**
     * Add a padding to the current position
     *
     * @param width The width of the padding
     * @param height The height of the padding
     */
    fun pad(
        width: Float,
        height: Float,
    ) {
        padX(width)
        padY(height)
    }

    /** Break and move to the next line */
    fun newLine() {
        currentX = Config.pageMinX
        currentY -= lastLineHeight
        lastLineHeight = 0f
    }
}
