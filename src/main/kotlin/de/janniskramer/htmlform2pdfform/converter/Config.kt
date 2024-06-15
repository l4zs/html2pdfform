package de.janniskramer.htmlform2pdfform.converter

data class Config(
    val inputWidth: Float = DEFAULT_INPUT_WIDTH,
    val fontSize: Float = DEFAULT_FONT_SIZE,
    val pageWidth: Float = DEFAULT_PAGE_WIDTH,
    val pageHeight: Float = DEFAULT_PAGE_HEIGHT,
    val pagePaddingX: Float = DEFAULT_PAGE_PADDING_X,
    val pagePaddingY: Float = DEFAULT_PAGE_PADDING_Y,
    val objectPadding: Float = DEFAULT_OBJECT_PADDING,
    val innerObjectPadding: Float = DEFAULT_INNER_OBJECT_PADDING,
) {
    companion object {
        private const val DEFAULT_INPUT_WIDTH = 200f
        private const val DEFAULT_FONT_SIZE = 16f
        private const val DEFAULT_PAGE_WIDTH = 595f
        private const val DEFAULT_PAGE_HEIGHT = 842f
        private const val DEFAULT_PAGE_PADDING_X = 50f
        private const val DEFAULT_PAGE_PADDING_Y = 50f
        private const val DEFAULT_OBJECT_PADDING = 20f
        private const val DEFAULT_INNER_OBJECT_PADDING = 5f
        private const val TEXT_RECT_PADDING_X = 0.01f
    }

    val pageMinX = pagePaddingX
    val pageMaxX = pageWidth - pagePaddingX
    val pageMinY = pagePaddingY
    val pageMaxY = pageHeight - pagePaddingY
    val effectivePageWidth = pageMaxX - pageMinX
    val effectivePageHeight = pageMaxY - pageMinY
    val textRectPaddingX = TEXT_RECT_PADDING_X // padding so that rects are a tiny bit wider than the text
}
