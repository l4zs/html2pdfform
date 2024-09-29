package de.l4zs.html2pdfform.backend.extension

import de.l4zs.html2pdfform.backend.data.Rectangle
import com.lowagie.text.Rectangle as PdfRectangle

/**
 * Converts a [Rectangle] to a [PdfRectangle].
 *
 * @return The [PdfRectangle] representation of the [Rectangle].
 * @receiver The [Rectangle] to convert.
 */
fun Rectangle.toPdfRectangle(): PdfRectangle = PdfRectangle(llx, lly, urx, ury)
