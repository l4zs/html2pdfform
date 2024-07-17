package de.janniskramer.htmlform2pdfform.config

import com.lowagie.text.Font
import com.lowagie.text.pdf.BaseFont

data class Config(
    val pageWidth: Float = DEFAULT_PAGE_WIDTH,
    val pageHeight: Float = DEFAULT_PAGE_HEIGHT,
    val pagePaddingX: Float = DEFAULT_PAGE_PADDING_X,
    val pagePaddingY: Float = DEFAULT_PAGE_PADDING_Y,
    val groupPaddingX: Float = DEFAULT_GROUP_PADDING_X,
    val groupPaddingY: Float = DEFAULT_GROUP_PADDING_Y,
    val innerPaddingX: Float = DEFAULT_INNER_PADDING_X,
    val innerPaddingY: Float = DEFAULT_INNER_PADDING_Y,
    val inputWidth: Float = DEFAULT_INPUT_WIDTH,
    val fontSize: Float = DEFAULT_FONT_SIZE,
    val selectSize: Int = DEFAULT_SELECT_SIZE,
    val textareaRows: Int = DEFAULT_TEXTAREA_ROWS,
    val maxRadiosPerRow: Int = DEFAULT_MAX_RADIOS_PER_ROW,
    val textRectPaddingX: Float = DEFAULT_TEXT_RECT_PADDING_X,
    val header: HeaderFooter = HeaderFooter(null, null, false),
    val footer: HeaderFooter = HeaderFooter(null, null, false),
    val metadata: Metadata = Metadata("Jannis Kramer", "Jannis Kramer", "Default Subject"),
) {
    companion object {
        private const val DEFAULT_PAGE_WIDTH = 595f
        private const val DEFAULT_PAGE_HEIGHT = 842f
        private const val DEFAULT_PAGE_PADDING_X = 50f
        private const val DEFAULT_PAGE_PADDING_Y = 50f
        private const val DEFAULT_GROUP_PADDING_X = 0f
        private const val DEFAULT_GROUP_PADDING_Y = 20f
        private const val DEFAULT_INNER_PADDING_X = 5f
        private const val DEFAULT_INNER_PADDING_Y = 5f
        private const val DEFAULT_INPUT_WIDTH = DEFAULT_PAGE_WIDTH - 2 * DEFAULT_PAGE_PADDING_X
        private const val DEFAULT_FONT_SIZE = 20f
        private const val DEFAULT_SELECT_SIZE = 4 // default number of visible options in a select
        private const val DEFAULT_TEXTAREA_ROWS = 3 // default number of rows in a textarea
        private const val DEFAULT_TEXT_RECT_PADDING_X = 0.01f
        private const val DEFAULT_MAX_RADIOS_PER_ROW = 4 // maximum number of radio buttons in a row
    }

    val fontHeight: Float = fontSize + innerPaddingY
    val boxSize: Float = fontSize
    val pageMinX = pagePaddingX
    val pageMaxX = pageWidth - pagePaddingX
    val pageMinY = pagePaddingY
    val pageMaxY = pageHeight - pagePaddingY
    val effectivePageWidth = pageMaxX - pageMinX
    val effectivePageHeight = pageMaxY - pageMinY

    val baseFont: BaseFont = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
    val defaultFont = Font(baseFont, fontSize)
    val defaultFontWidth = baseFont.getWidthPoint("a", fontSize)

    val zapfDingbatsFont: BaseFont = BaseFont.createFont(BaseFont.ZAPFDINGBATS, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
}
