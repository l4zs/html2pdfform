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
    val textRectPadding: Float = DEFAULT_TEXT_RECT_PADDING,
    val header: HeaderFooter = HeaderFooter(null, null, false),
    val footer: HeaderFooter = HeaderFooter(null, null, false),
    val metadata: Metadata = Metadata("Jannis Kramer", "Jannis Kramer", "Default Subject"),
    val intro: Intro =
        Intro(
            Image(
                "files/unims.png",
                200f,
                50f,
            ),
            Text(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc hendrerit, risus a pharetra congue, elit dui porttitor risus, eget faucibus neque nisl id neque. Praesent tempus, ante at placerat dapibus, magna ante faucibus quam, et tempor purus eros pellentesque erat. In sit amet quam quis nisl ultricies convallis. Praesent ac nunc varius, tincidunt urna in, sodales diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Vivamus non ipsum tellus. Donec convallis ornare dolor sit amet congue. Curabitur tristique dolor in ipsum tristique, et lobortis ligula euismod. Quisque a bibendum turpis. Mauris ac pulvinar risus. Nulla facilisi. Morbi condimentum, massa id tempus iaculis, est quam venenatis risus, sed commodo lorem lectus vel nulla.\n" +
                    "\n" +
                    "Nullam convallis ipsum a sem dapibus ultrices ut in neque. Mauris sollicitudin tempus placerat. Morbi mauris elit, commodo at convallis accumsan, consequat vitae libero. Nulla tincidunt laoreet felis. Nunc sagittis mi non eros mollis, non varius erat ultricies. Duis auctor rhoncus faucibus. Integer id dui risus. Duis id nisl tortor. Curabitur auctor congue ullamcorper. Pellentesque at ligula et nisi elementum faucibus et et magna.",
                8f,
            ),
        ),
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
        private const val DEFAULT_TEXT_RECT_PADDING = 0.01f
        private const val DEFAULT_MAX_RADIOS_PER_ROW = 4 // maximum number of radio buttons in a row
    }

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
