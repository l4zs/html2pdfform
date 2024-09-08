package de.l4zs.html2pdfform.config

import com.lowagie.text.Font
import com.lowagie.text.PageSize
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.BaseFont
import de.l4zs.html2pdfform.data.HeaderFooter
import de.l4zs.html2pdfform.data.Intro
import de.l4zs.html2pdfform.data.Metadata
import de.l4zs.html2pdfform.data.Text
import org.jsoup.Connection.Base

data class Config(
    val pageSize: Rectangle = PageSize.A4,
    val pagePaddingX: Float = 50f,
    val pagePaddingY: Float = 50f,
    val groupPaddingX: Float = 0f,
    val groupPaddingY: Float = 20f,
    val innerPaddingX: Float = 5f,
    val innerPaddingY: Float = 5f,
    val font: String = BaseFont.TIMES_ROMAN,
    val fontSize: Float = 20f,
    val selectSize: Int = 4,
    val textareaRows: Int = 3,
    val maxRadiosPerRow: Int = 4,
    val firstPageHeader: HeaderFooter? = null,
    val firstPageFooter: HeaderFooter? = null,
    val header: HeaderFooter = HeaderFooter("", "", false),
    val footer: HeaderFooter = HeaderFooter("Seite", "", true),
    val metadata: Metadata = Metadata("", "", ""),
    val intro: Intro = Intro(null, null),
) {

    val inputWidth: Float = pageSize.width - 2 * pagePaddingX
    val textRectPadding: Float = 0.01f
    val boxSize: Float = fontSize
    val pageMinX = pagePaddingX
    val pageMaxX = pageSize.width - pagePaddingX
    val pageMinY = pagePaddingY
    val pageMaxY = pageSize.height - pagePaddingY
    val effectivePageWidth = pageMaxX - pageMinX
    val effectivePageHeight = pageMaxY - pageMinY

    val baseFont: BaseFont = BaseFont.createFont(font, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
    val introFont: BaseFont = BaseFont.createFont(
        intro.text?.font ?: font,
        BaseFont.CP1252,
        BaseFont.NOT_EMBEDDED
    )
    val defaultFont = Font(baseFont, fontSize)
}
