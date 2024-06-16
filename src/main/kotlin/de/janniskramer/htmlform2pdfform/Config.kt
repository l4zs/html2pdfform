package de.janniskramer.htmlform2pdfform

import com.lowagie.text.Font
import com.lowagie.text.pdf.BaseFont

object Config {
    // DEFAULTS

    private const val DEFAULT_PAGE_WIDTH = 595f
    private const val DEFAULT_PAGE_HEIGHT = 842f
    private const val DEFAULT_PAGE_PADDING_X = 50f
    private const val DEFAULT_PAGE_PADDING_Y = 50f
    private const val DEFAULT_GROUP_PADDING_X = 0f
    private const val DEFAULT_GROUP_PADDING_Y = 20f
    private const val DEFAULT_INNER_PADDING_X = 5f
    private const val DEFAULT_INNER_PADDING_Y = 5f
    private const val DEFAULT_INPUT_WIDTH = 200f
    private const val DEFAULT_FONT_SIZE = 20f
    private const val DEFAULT_BOX_SIZE = DEFAULT_FONT_SIZE // radio button and checkbox size
    private const val DEFAULT_SELECT_SIZE = 4 // default number of visible options in a select
    private const val DEFAULT_TEXTAREA_ROWS = 3 // default number of rows in a textarea

    private const val TEXT_RECT_PADDING_X = 0.01f

    // values

    val pageWidth: Float = DEFAULT_PAGE_WIDTH
    val pageHeight: Float = DEFAULT_PAGE_HEIGHT
    val pagePaddingX: Float = DEFAULT_PAGE_PADDING_X
    val pagePaddingY: Float = DEFAULT_PAGE_PADDING_Y
    val groupPaddingX: Float = DEFAULT_GROUP_PADDING_X
    val groupPaddingY: Float = DEFAULT_GROUP_PADDING_Y
    val innerPaddingX: Float = DEFAULT_INNER_PADDING_X
    val innerPaddingY: Float = DEFAULT_INNER_PADDING_Y
    val inputWidth: Float = DEFAULT_INPUT_WIDTH
    val fontSize: Float = DEFAULT_FONT_SIZE
    val fontHeight: Float = fontSize + innerPaddingY // height of a line of text including padding
    val boxSize: Float = DEFAULT_BOX_SIZE
    val selectSize: Int = DEFAULT_SELECT_SIZE
    val textareaRows: Int = DEFAULT_TEXTAREA_ROWS

    val pageMinX = pagePaddingX
    val pageMaxX = pageWidth - pagePaddingX
    val pageMinY = pagePaddingY
    val pageMaxY = pageHeight - pagePaddingY
    val effectivePageWidth = pageMaxX - pageMinX
    val effectivePageHeight = pageMaxY - pageMinY
    val textRectPaddingX = TEXT_RECT_PADDING_X // padding so that rects are a tiny bit wider than the text

    val baseFont: BaseFont = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
    val defaultFont = Font(baseFont, fontSize)
    val defaultFontWidth = baseFont.getWidthPoint("a", fontSize)
}
