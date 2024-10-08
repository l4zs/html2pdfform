package de.l4zs.html2pdfform.backend.converter

import com.lowagie.text.Document
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.data.Rectangle

/**
 * Location handler for placing elements on the page. The location handler
 * keeps track of the current position on the page and provides methods to
 * calculate the position of the next element.
 *
 * @constructor Create empty Location handler
 * @property pdf The PDF document to place elements on
 * @property config The configuration of the PDF
 */
class LocationHandler(
    private val pdf: Document,
    private val config: Config,
) {
    var currentX = config.pageMinX
    var currentY = config.pageMaxY

    /** Create new page and resets position */
    private fun newPage() {
        pdf.newPage()
        currentX = config.pageMinX
        currentY = config.pageMaxY
    }

    /**
     * Add a padding to the current position
     *
     * @param width The width of the padding
     */
    private fun padX(width: Float) {
        currentX += width
    }

    /**
     * Add a padding to the current position
     *
     * @param height The height of the padding
     */
    private fun padY(height: Float) {
        currentY -= height
    }

    /**
     * Add a padding to the current position
     *
     * @param width The width of the padding
     * @param height The height of the padding
     */
    private fun pad(
        width: Float,
        height: Float,
    ) {
        padX(width)
        padY(height)
    }

    /**
     * Return if an element with the given width would fit on the current page
     *
     * @param width The width of the element
     * @return true if an element with the given width would fit, false
     *    otherwise
     */
    private fun wouldFitOnPageX(width: Float): Boolean = currentX + width <= config.pageMaxX

    /**
     * Return if an element with the given height would fit on the current page
     *
     * @param height The height of the element
     * @return true if an element with the given height would fit, false
     *    otherwise
     */
    private fun wouldFitOnPageY(height: Float): Boolean = currentY - height >= config.pageMinY

    /**
     * Adjusts the rectangle to the current position and updates the current
     * position accordingly
     *
     * @param rectangle The rectangle to adjust
     * @return The adjusted rectangle
     */
    fun adjustRectangle(rectangle: Rectangle): Rectangle {
        if (!wouldFitOnPageY(rectangle.height)) {
            newPage()
        }

        return rectangle
            .move(
                currentX,
                currentY - rectangle.height,
            ).also {
                currentY = it.lly - config.groupPaddingY
            }
    }
}
