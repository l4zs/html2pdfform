package de.l4zs.html2pdfform.config

import com.lowagie.text.Font
import com.lowagie.text.PageSize
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.BaseFont
import de.l4zs.html2pdfform.data.HeaderFooter
import de.l4zs.html2pdfform.data.Intro
import de.l4zs.html2pdfform.data.Metadata
import de.l4zs.html2pdfform.data.Text

data class Config(
    val pageSize: Rectangle = PageSize.A4,
    val pagePaddingX: Float = 50f,
    val pagePaddingY: Float = 50f,
    val groupPaddingX: Float = 0f,
    val groupPaddingY: Float = 20f,
    val innerPaddingX: Float = 5f,
    val innerPaddingY: Float = 5f,
    val inputWidth: Float = pageSize.width - 2 * pagePaddingX,
    val fontSize: Float = 20f,
    val selectSize: Int = 4,
    val textareaRows: Int = 3,
    val maxRadiosPerRow: Int = 4,
    val firstPageHeader: HeaderFooter? = HeaderFooter("", "", false),
    val firstPageFooter: HeaderFooter? = HeaderFooter("", "", false),
    val header: HeaderFooter = HeaderFooter("", "", false),
    val footer: HeaderFooter = HeaderFooter("Seite ", "", true),
    val metadata: Metadata = Metadata("Autor", "Ersteller", "Titel"),
    val intro: Intro =
        Intro(
            null,
//            Image(
//                "files/unims.png",
//                200f,
//                50f,
//            ),
            Text(
                "Hier könnte ein kurzer Text stehen, der benötigten Kontext, Anweisungen oder ähnliches enthält.",
                8f,
            ),
        ),
) {

    val textRectPadding: Float = 0.01f
    val boxSize: Float = fontSize
    val pageMinX = pagePaddingX
    val pageMaxX = pageSize.width - pagePaddingX
    val pageMinY = pagePaddingY
    val pageMaxY = pageSize.height - pagePaddingY
    val effectivePageWidth = pageMaxX - pageMinX
    val effectivePageHeight = pageMaxY - pageMinY

    val baseFont: BaseFont = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
    val defaultFont = Font(baseFont, fontSize)
    val defaultFontWidth = baseFont.getWidthPoint("a", fontSize)

    val zapfDingbatsFont: BaseFont = BaseFont.createFont(BaseFont.ZAPFDINGBATS, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
}
