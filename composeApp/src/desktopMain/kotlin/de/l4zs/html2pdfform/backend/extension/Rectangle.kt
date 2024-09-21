package de.l4zs.html2pdfform.backend.extension

import de.l4zs.html2pdfform.backend.data.Rectangle
import com.lowagie.text.Rectangle as PdfRectangle

fun Rectangle.toPdfRectangle(): PdfRectangle = PdfRectangle(llx, lly, urx, ury)
